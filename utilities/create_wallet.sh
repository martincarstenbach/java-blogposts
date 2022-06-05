#!/usr/bin/env bash

# Copyright 2022 Martin Bach
#  * 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#  * 
#     http://www.apache.org/licenses/LICENSE-2.0
#  * 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -euo pipefail

# GLOBALS
WALLET_LOC=${HOME}/tns

# MAIN

echo
echo "About to create a new external password store in ${WALLET_LOC}"
echo

[[ ! $(/usr/bin/uname) = 'Linux' ]] && {
	echo "ERR: this script must run on Linux"
	exit 1
}

[[ -z ${ORACLE_HOME} ]] && {
	echo "ERR: you must set ORACLE_HOME before invoking this script"
	exit 1
}

[[ -d "${WALLET_LOC}" ]] && {
	echo "ERR: a TNS directory exists in ${WALLET_LOC}. I'm not overwriting it, exiting."
	exit 1
}

read -sp "Enter the wallet password: " wallet_pwd
echo
read -p "Enter the service name to connect to (in lower case): " service_name
read -p "Enter the user to connect to: " username
read -sp "Enter the user's password: " user_pwd
echo
read -p "Enter the hostname/scan of your RAC/single instance database: " host_name
read -p "Enter the (SCAN) listener port (defaults to 1521): " listner_port

/usr/bin/mkdir "${WALLET_LOC}" || {
	echo "ERR: could not create ${WALLET_LOC} for some reason, weird. Exiting"
	exit 1
}

${ORACLE_HOME}/bin/mkstore -wrl "${WALLET_LOC}" -create <<EOF
${wallet_pwd}
${wallet_pwd}
EOF

[[ ${?} -ne 0 ]] && {
	echo "ERR: the creation of the external password store failed"
	exit 1
}

${ORACLE_HOME}/bin/mkstore -wrl "${WALLET_LOC}" -createCredential "${service_name}" "${username}" <<EOF
${user_pwd}
${user_pwd}
${wallet_pwd}
EOF

[[ ${?} -ne 0 ]] && {
	echo "ERR: the addition of credentials to the external password store failed"
	exit 1
}

# create the tnsnames.ora file
# there is no need for a sqlnet.ora file thanks to the ojdbc.properties file 

cat > "${WALLET_LOC}/tnsnames.ora" <<EOF
${service_name} =
  (DESCRIPTION =
    (ADDRESS = (PROTOCOL = TCP)(HOST = ${host_name})(PORT = ${listener_port:-1521}))
    (CONNECT_DATA =
      (SERVER = DEDICATED)
      (SERVICE_NAME = ${service_name})
    )
  )
EOF


# create ojdbc.properties
echo 'oracle.net.wallet_location=(source=(method=file)(method_data=(directory=${TNS_ADMIN})))' > "${WALLET_LOC}/ojdbc.properties"

unset wallet_pwd
unset username
unset user_pwd
unset host_name
unset service_name
unset listener_port

echo
echo creation of the tns directory complete, now transfer it to the application client
echo


