package gg.manny.forums.web.controller.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.manny.forums.Application;
import gg.manny.forums.rank.RankRepository;
import gg.manny.forums.user.User;
import gg.manny.forums.user.UserRepository;
import gg.manny.forums.user.grant.Grant;
import gg.manny.forums.user.punishment.Punishment;
import gg.manny.forums.user.service.UserService;
import gg.manny.forums.util.MojangUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

@RestController
public class PlayerRestController {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private RankRepository rankRepository;


    @RequestMapping(value = "/api/v1/player", method = RequestMethod.POST)
    public String getData(HttpServletRequest request) {
        JsonObject data = new JsonObject();


        User user = null;

        String type = request.getParameter("type");
        String value = request.getParameter("value");
        switch (type) {
            case "UUID": {
                user = userRepository.findById(UUID.fromString(value)).orElse(null);
                break;
            }
            case "USERNAME": {
                user = userRepository.findByUsernameIgnoreCase(value);
                break;
            }
            case "EMAIL": {
                user = userRepository.findByEmail(value);
                break;
            }
        }

        data.addProperty("success", user != null);

        JsonObject playerData = new JsonObject();
        if (user != null) {
            JsonArray grants = new JsonArray(), punishments = new JsonArray();
            for (Grant grant : user.getGrants()) grants.add(grant.toJson());
            for (Punishment punishment : user.getPunishments()) punishments.add(punishment.toJson());

            playerData.addProperty("id", user.getId().toString());
            playerData.addProperty("username", user.getUsername());
            playerData.addProperty("email", user.getEmail());
            playerData.addProperty("registered", user.isRegistered());
            playerData.addProperty("dateJoined", user.getDateJoined().getTime());
            playerData.addProperty("dateLastSeen", user.getDateLastSeen().getTime());

            playerData.addProperty("grants", grants.toString());
            playerData.addProperty("punishments", punishments.toString());
        }


        data.add("player", playerData);
        return Application.GSON.toJson(data);
    }

    @RequestMapping(value = "/api/v1/player/save", method = RequestMethod.POST)
    public String saveProfile(HttpServletRequest request) {
        JsonObject data = new JsonParser().parse(request.getParameter("data")).getAsJsonObject();
        UUID id = UUID.fromString(data.get("id").getAsString());
        User user = userRepository.findById(id).orElse(new User());
        boolean error = false;

        if (user.getId() == null) {
            try {
                user.setId(id);
                user.setUsername(MojangUtils.fetchName(id));
                user.setDateJoined(new Date(System.currentTimeMillis()));
                user.setDateLastSeen(new Date(System.currentTimeMillis()));
            } catch (Exception e) {
                error = true;
            }
        }

        if (!error) {
            if (data.has("dateLastSeen")) user.setDateLastSeen(new Date(data.get("dateLastSeen").getAsLong()));

            if (data.has("grants")) {
                user.getGrants().clear();
                for (JsonElement element : new JsonParser().parse(data.get("grants").getAsString()).getAsJsonArray()) {
                    user.addGrant(
                        new Grant(
                            element.getAsJsonObject(),
                            id,
                            rankRepository.findByName(element.getAsJsonObject().get("rank").getAsString())
                        )
                    );
                }
            }

            if (data.has("punishments")) {
                user.getPunishments().clear();
                for (JsonElement element : new JsonParser().parse(data.get("punishments").getAsString()).getAsJsonArray()) {
                    user.addPunishment(new Punishment(element.getAsJsonObject()));
                }}
        }

        userService.save(user);

        JsonObject response = new JsonObject();
        response.addProperty("success", !error);
        return Application.GSON.toJson(response);
    }
}
