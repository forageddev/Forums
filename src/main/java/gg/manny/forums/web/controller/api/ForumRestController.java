package gg.manny.forums.web.controller.api;

import com.google.gson.JsonObject;
import gg.manny.forums.Application;
import gg.manny.forums.forum.Forum;
import gg.manny.forums.forum.ForumCategory;
import gg.manny.forums.forum.repository.CategoryRepository;
import gg.manny.forums.forum.repository.ForumRepository;
import gg.manny.forums.forum.service.impl.CategoryService;
import gg.manny.forums.forum.service.impl.ForumService;
import gg.manny.forums.rank.RankRepository;
import gg.manny.forums.user.User;
import gg.manny.forums.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
public class ForumRestController {

    @Autowired private UserRepository userRepository;
    @Autowired private ForumService forumService;
    @Autowired private CategoryService categoryService;
    @Autowired private CategoryRepository categoryRepository;

    @RequestMapping(value = "/api/v1/forums/{id}/create-forum", method = RequestMethod.GET)
    public String createForum(@PathVariable String id) {
        Forum forum = new Forum();
        forum.setName(id);
        forum.setWeight(1);

        List<ForumCategory> categories = new ArrayList<>();
        ForumCategory category = new ForumCategory();
        category.setId(id.toLowerCase());
        category.setDisplayName(id);
        category.setDescription("Test");
        category.setForum(forum);
        category.setWeight(1);
        categoryRepository.save(category);

        categories.add(category);
        forum.setCategories(categories);

        forumService.addForum(forum);
        return "Created forum";
    }

    @RequestMapping(value = "/api/v1/forums/{id}/create-category/{name}", method = RequestMethod.GET)
    public String loadProfile(@PathVariable String id, @PathVariable String name) {
        Forum forum = forumService.getForum(id);

        ForumCategory category = new ForumCategory();
        category.setId(name.toLowerCase());
        category.setDisplayName(name);
        category.setDescription("Test");
        category.setForum(forum);
        category.setWeight(1);
        forum.getCategories().add(category);
        forumService.addForum(forum);

        return "Created new forum category";
    }
}
