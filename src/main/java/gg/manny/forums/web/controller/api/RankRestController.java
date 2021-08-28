package gg.manny.forums.web.controller.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.manny.forums.Application;
import gg.manny.forums.rank.Rank;
import gg.manny.forums.rank.RankRepository;
import gg.manny.forums.user.User;
import gg.manny.forums.user.UserRepository;
import gg.manny.forums.user.grant.Grant;
import gg.manny.forums.user.punishment.Punishment;
import gg.manny.forums.user.punishment.PunishmentType;
import gg.manny.forums.user.service.UserService;
import gg.manny.forums.util.MojangUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

@RestController
public class RankRestController {

    @Autowired private RankRepository rankRepository;

    @RequestMapping(value = "/api/v1/ranks", method = RequestMethod.POST)
    public String getData() {
        JsonObject data = new JsonObject();

        JsonArray rankData = new JsonArray();
        rankRepository.findAll().forEach(rank -> {
            System.out.println(rank.getId());
            rankData.add(rank.toJson());
        });

        data.add("ranks", rankData);
        return Application.GSON.toJson(data);
    }

    @RequestMapping(value = "/api/v1/rank/create", method = RequestMethod.POST)
    public String createRank(HttpServletRequest request) {
        String name = request.getParameter("name");

        JsonObject response = new JsonObject();


        if (rankRepository.findById(name).orElse(null) == null) {
            Rank rank = new Rank();
            rank.setId(name);
            rank.setName(name);
            rankRepository.save(rank);
            response.addProperty("success", true);
        } else {
            response.addProperty("success", false);
        }
        return Application.GSON.toJson(response);
    }

    @RequestMapping(value = "/api/v1/rank/update", method = RequestMethod.POST)
    public String updateRank(HttpServletRequest request) {
        String name = request.getParameter("name");

        JsonObject response = new JsonObject();

        response.addProperty("success", rankRepository.findById(name).isPresent());

        if (rankRepository.findById(name).isPresent()) {
            Rank rank = rankRepository.findById(name).orElse(null);

            JsonObject data = new JsonParser().parse(request.getParameter("data")).getAsJsonObject();

            if (data.has("name")) rank.setName(data.get("name").getAsString());
            if (data.has("prefix")) rank.setPrefix(data.get("prefix").getAsString());
            if (data.has("color")) rank.setColor(data.get("color").getAsString());
            if (data.has("forumColor")) rank.setForumColor(data.get("forumColor").getAsString());
            if (data.has("weight")) rank.setWeight(data.get("weight").getAsInt());
            rankRepository.save(rank);
        }
        return Application.GSON.toJson(response);
    }

    @RequestMapping(value = "/api/v1/rank/delete", method = RequestMethod.POST)
    public String deleteRank(HttpServletRequest request) {
        String name = request.getParameter("name");

        JsonObject response = new JsonObject();

        response.addProperty("success", rankRepository.findById(name).isPresent());

        if (rankRepository.findById(name).isPresent()) {
            rankRepository.deleteById(name);
        }
        return Application.GSON.toJson(response);
    }
}
