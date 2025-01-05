package offline.simple.messenger.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UIController {
    @RequestMapping("/")
    public String index() {
        return "forward:/index.html"; // Vue 빌드 결과물의 index.html
    }


}
