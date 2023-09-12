package de.theplayground.demo01;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/hits")
public class SessionHitCounterController {

    private static final Logger LOG = LoggerFactory.getLogger(SessionHitCounterController.class);

    @Autowired
    SessionHitCounterService shcs;

    @GetMapping
    public String processGetRequest(Model model, HttpSession httpSession, HttpServletRequest req) {
        SessionHitCounter shc = null;

        String sessionID = (String) httpSession.getAttribute("sessionID");
        if (sessionID == null) {
            String newSessionID = UUID.randomUUID().toString();
            httpSession.setAttribute("sessionID", newSessionID);
            shc = shcs.incrementCount(newSessionID, req.getHeader("User-Agent"));
        } else {
            shc = shcs.incrementCount(sessionID, req.getHeader("User-Agent"));
        }

        model.addAttribute("sessionHitCounter", shc);

        return "hits";
    }

    @PostMapping
    public String processPostRequest(Model model, @ModelAttribute SessionHitCounter sessionHitCounter) {
        sessionHitCounter.setHitCount(
                shcs.incrementCount(sessionHitCounter.getSessionID())
        );
        return "hits";
    }
}
