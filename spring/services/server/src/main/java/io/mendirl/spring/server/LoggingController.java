package io.mendirl.spring.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/logging")
public class LoggingController {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingController.class);

    @GetMapping
    public void log() {
        LOG.info("Some useful log message");
    }
}
