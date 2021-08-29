package gg.manny.forums.web.controller;

import gg.manny.forums.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LeaderboardController {

    @Autowired private UserRepository userController;

    @RequestMapping(value = {"/leaderboards"}, method = RequestMethod.GET)
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();


        modelAndView.addObject("users", userController.findAll());
        modelAndView.addObject("controller", userController);

        modelAndView.setViewName("leaderboards");
        return modelAndView;
    }
}
