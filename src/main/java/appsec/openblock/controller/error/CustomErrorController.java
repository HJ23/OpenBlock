package appsec.openblock.controller.error;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping("/404")
    public ModelAndView handleError(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("404");
        return mav;
    }

    @RequestMapping("/403")
    public ModelAndView handleForbidden(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("403");
        return mav;
    }


}
