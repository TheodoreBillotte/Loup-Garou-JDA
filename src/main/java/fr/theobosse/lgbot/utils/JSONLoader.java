package fr.theobosse.lgbot.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.game.Role;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JSONLoader {

    private JsonNode json;
    private ObjectMapper mapper;

    public JSONLoader(String path) throws JsonProcessingException {
        mapper = new ObjectMapper();
        json = mapper.readTree(openJSON(path));
    }

    private String openJSON(String path) {
        try {
            Stream<String> lines = Files.lines(
                    Paths.get(ClassLoader.getSystemResource(path).toURI()));
            return lines.collect(Collectors.joining());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadData(Game game, String save) {
        JsonNode node = json.get(game.getHost().getId()).get(save);
        game.setName(save);
        game.getOptions().setNightTime(node.get("nightTime").asInt());
        game.getOptions().setDayTime(node.get("dayTime").asInt());
        game.getOptions().setMaxPlayers(node.get("maxPlayers").asInt());
        game.getOptions().setNightMute(node.get("nightMute").asBoolean());
        game.getOptions().setDeadAreMuted(node.get("deadAreMuted").asBoolean());

        game.getUtils().getRoles().clear();
        for (JsonNode role : node.get("roles"))
            game.getUtils().addRole(Roles.getRoleBySub(role.asText()));
    }

    public void removeSave(Game game, String save) {
        ObjectNode node = (ObjectNode) json.get(game.getHost().getId());
        node.remove(save);

        try {
            Files.write(Paths.get(ClassLoader.getSystemResource("saves.json").toURI()), mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json).getBytes());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void saveData(Game game) {
        ObjectNode node = (ObjectNode) json;
        if (json.get(game.getHost().getId()) == null)
            node.set(game.getHost().getId(), mapper.createObjectNode());

        node = (ObjectNode) json.get(game.getHost().getId());
        node.set(game.getName(), mapper.createObjectNode());
        node = (ObjectNode) json.get(game.getHost().getId()).get(game.getName());
        node.set("nightTime", mapper.valueToTree(game.getOptions().getNightTime()));
        node.set("dayTime", mapper.valueToTree(game.getOptions().getDayTime()));
        node.set("maxPlayers", mapper.valueToTree(game.getOptions().getMaxPlayers()));
        node.set("nightMute", mapper.valueToTree(game.getOptions().isNightMute()));
        node.set("deadAreMuted", mapper.valueToTree(game.getOptions().isDeadAreMuted()));
        node.set("roles", mapper.valueToTree(game.getUtils().getRoles().stream().map(Role::getSubName).collect(Collectors.toList())));

        try {
            Files.write(Paths.get(ClassLoader.getSystemResource("saves.json").toURI()), mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json).getBytes());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public StringSelectMenu getSaves(Player player, String id) {
        Iterator<String> it = json.get(player.getMember().getId()).fieldNames();
        if (!it.hasNext())
            return null;
        StringSelectMenu.Builder builder = StringSelectMenu.create(id).setPlaceholder("Choisissez une sauvegarde");
        while (it.hasNext()) {
            String save = it.next();
            builder.addOption(save, save);
        }
        return builder.build();
    }

    public JsonNode getJson() {
        return json;
    }

}
