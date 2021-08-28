package gg.manny.forums.web.controller;

import gg.manny.forums.forum.repository.CategoryRepository;
import gg.manny.forums.forum.repository.ForumRepository;
import gg.manny.forums.forum.repository.ThreadRepository;
import gg.manny.forums.rank.Rank;
import gg.manny.forums.rank.RankRepository;
import gg.manny.forums.user.UserRepository;
import gg.manny.forums.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class StaffController {

    @Autowired private UserRepository userController;
    @Autowired private RankRepository rankRepository;

    @RequestMapping(value = {"/staff"}, method = RequestMethod.GET)
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();


        modelAndView.addObject("users", userController.findAll());
        modelAndView.addObject("ranks", rankRepository.findAll().stream().sorted((o1, o2) -> o2.getWeight() - o1.getWeight()).collect(Collectors.toList()));
        modelAndView.addObject("controller", userController);

        modelAndView.setViewName("staff");
        return modelAndView;
    }
}
