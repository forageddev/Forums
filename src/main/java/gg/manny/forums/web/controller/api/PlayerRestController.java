package gg.manny.forums.web.controller.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
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
import org.apache.commons.lang.StringEscapeUtils;
import org.bson.json.JsonWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.StringReader;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class PlayerRestController {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private RankRepository rankRepository;


    @RequestMapping(value = "/api/v1/players/{id}", method = RequestMethod.GET)
    public String getData(@PathVariable UUID id) {
        JsonObject data = new JsonObject();

        User user = userRepository.findById(id).orElse(null);
        data.addProperty("success", user != null);

        JsonObject playerData = new JsonObject();
        if (user != null) {
            JsonArray grants = new JsonArray(), punishments = new JsonArray();
            for (Grant grant : user.getGrants()) grants.add(grant.toJson());
            for (Punishment punishment : user.getPunishments()) punishments.add(punishment.toJson());

            playerData.addProperty("id", id.toString());
            playerData.addProperty("username", user.getUsername());

            playerData.addProperty("email", user.getEmail());
            playerData.addProperty("registered", user.isRegistered());
            playerData.addProperty("grants", grants.toString());
            playerData.addProperty("punishments", punishments.toString());
        }


        data.add("player", playerData);
        return Application.GSON.toJson(data);
    }

    @RequestMapping(value = "/api/v1/players/{id}/save", method = RequestMethod.POST)
    public String saveProfile(@PathVariable UUID id, @RequestBody String d) {
        JsonObject data = new JsonParser().parse(d).getAsJsonObject();
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
            if (data.has("grants")) {
                user.getGrants().clear();
                data.get("grants").getAsJsonArray().forEach(element -> user.getGrants().add(new Grant(element.getAsJsonObject(), rankRepository.findByName(element.getAsJsonObject().get("group").getAsString()))));
            }

            if (data.has("punishments")) {
                user.getPunishments().clear();
                data.get("punishments").getAsJsonArray().forEach(element -> user.getPunishments().add(new Punishment(element.getAsJsonObject())));
            }
        }

        userService.save(user);



        JsonObject response = new JsonObject();
        response.addProperty("success", !error);
        response.add("values", data);
        return Application.GSON.toJson(response);
    }

    @RequestMapping(value = "/api/v1/players/{id}/addGrant/{rank}/{reason}", method = RequestMethod.GET)
    public String addGrant(@PathVariable UUID id, @PathVariable Rank rank, @PathVariable String reason) {
        User user = userRepository.findById(id).orElse(null);



        Grant grant = new Grant();
        grant.setId(UUID.randomUUID());
        grant.setRank(rank);
        grant.setReason(reason);
        grant.setIssuedBy(UUID.fromString("392b3a27-d17f-4a02-be8a-2302726946c1"));
        grant.setDuration(Long.MAX_VALUE);
        user.addGrant(grant);
        userRepository.save(user);

        return "Grant added";
    }

    @RequestMapping(value = "/api/v1/players/{id}/addPunishment/{type}/{reason}", method = RequestMethod.GET)
    public String addPunishment(@PathVariable UUID id, @PathVariable String type, @PathVariable String reason) {
        User user = userRepository.findById(id).orElse(null);

        Punishment punishment = new Punishment();
        punishment.setType(PunishmentType.valueOf(type));
        punishment.setReason(reason);
        punishment.setIssuedBy(UUID.fromString("392b3a27-d17f-4a02-be8a-2302726946c1"));
        user.addPunishment(punishment);
        userRepository.save(user);

        return "Punishment added";
    }
}
