#!/usr/bin/env bash

# Copyright 2022 Martin Bach
#  
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#   
#     http://www.apache.org/licenses/LICENSE-2.0
#  
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -euo pipefail

# ------------------------------------------------------------------------------------- NOTES

# This script creates a new secure external password store in ~/tns. If one has been created
# previously, the script aborts

# In case the database has a db_domain set, it is important not to specify it as part of 
# mkstore -createCredential. The script should take care of that.

# ------------------------------------------------------------------------------------- GLOBALS

WALLET_LOC=${1:-${HOME}/tns}

# ------------------------------------------------------------------------------------- MAIN

echo
echo "About to create a new secure external password store in ${WALLET_LOC}"
echo

[[ ! $(/usr/bin/uname) = 'Linux' ]] && {
	echo "ERR: this script must run on Linux"
	exit 1
}

[[ -z ${ORACLE_HOME} ]] && {
	echo "ERR: ORACLE_HOME must point to a client or server installation (not instant client)"
	exit 1
}

[[ -d "${WALLET_LOC}" ]] && {
	echo "ERR: a TNS directory exists in ${WALLET_LOC}. I'm not overwriting it, exiting."
	exit 1
}

read -sp "Enter the wallet password: " wallet_pwd
echo
read -p "Enter the service name to connect to (in lower case, w/o db_domain): " tns_alias
read -p "Enter a db_domain (leave blank if there is none): " db_domain
read -p "Enter the user to connect to: " username
read -sp "Enter the user's password: " user_pwd
echo
read -p "Enter the hostname/scan of your RAC/single instance database: " host_name
read -p "Enter the (SCAN) listener port (defaults to 1521): " listener_port

/usr/bin/mkdir -vp "${WALLET_LOC}" || {
	echo "ERR: could not create ${WALLET_LOC} for some reason, weird. Exiting"
	exit 1
}

# create the wallet

${ORACLE_HOME}/bin/mkstore -wrl "${WALLET_LOC}" -create <<EOF
${wallet_pwd}
${wallet_pwd}
EOF

[[ ${?} -ne 0 ]] && {
	echo "ERR: the creation of the external password store failed"
	exit 1
}

# add credentials

${ORACLE_HOME}/bin/mkstore -wrl "${WALLET_LOC}" -createCredential "${tns_alias}" "${username}" <<EOF
${user_pwd}
${user_pwd}
${wallet_pwd}
EOF

[[ ${?} -ne 0 ]] && {
	echo "ERR: the addition of credentials to the external password store failed"
	exit 1
}

# create the tnsnames.ora file

if [ -z "${db_domain}" ]; then
	service_name="${tns_alias}"
else
	service_name="${tns_alias}.${db_domain}"
fi

cat > "${WALLET_LOC}/tnsnames.ora" <<EOF
${tns_alias} =
  (DESCRIPTION =
    (ADDRESS = (PROTOCOL = TCP)(HOST = ${host_name})(PORT = ${listener_port:-1521}))
    (CONNECT_DATA =
      (SERVER = DEDICATED)
      (service_name = ${service_name})
    )
  )
EOF

# create a sqlnet.ora file in case the OCI driver is needed

cat > "${WALLET_LOC}/sqlnet.ora" <<EOF
WALLET_LOCATION =
  (SOURCE =(METHOD = FILE)
    (METHOD_DATA =
      (DIRECTORY = ${WALLET_LOC})
    )
  )

SQLNET.WALLET_OVERRIDE = TRUE
EOF

# create ojdbc.properties

echo 'oracle.net.wallet_location=(source=(method=file)(method_data=(directory=${TNS_ADMIN})))' > "${WALLET_LOC}/ojdbc.properties"

unset wallet_pwd
unset username
unset user_pwd
unset host_name
unset tns_alias
unset listener_port

echo
echo creation of the tns directory complete, now transfer it to the application client
echo