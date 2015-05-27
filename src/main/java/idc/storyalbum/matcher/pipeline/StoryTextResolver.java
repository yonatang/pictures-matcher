package idc.storyalbum.matcher.pipeline;

import freemarker.template.TemplateException;
import idc.storyalbum.matcher.exception.TemplateErrorException;
import idc.storyalbum.matcher.freemarker.FreeMarkerTemplate;
import idc.storyalbum.matcher.freemarker.context.Context;
import idc.storyalbum.matcher.freemarker.context.Page;
import idc.storyalbum.model.album.Album;
import idc.storyalbum.model.album.AlbumPage;
import idc.storyalbum.model.profile.Character;
import idc.storyalbum.model.profile.Location;
import idc.storyalbum.model.profile.Profile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * Created by yonatan on 25/4/2015.
 */
@Slf4j
@Service
public class StoryTextResolver {
    @Autowired
    FreeMarkerTemplate freeMarkerTemplate;

    @Value("${story-album.subject-name}")
    private String subjectName;

    private Context createContext(Album album, Profile profile) {
        Map<String, Character> charIdToChar = profile.getCharacters()
                .stream()
                .collect(toMap((x) -> x.getId(), identity()));
        profile.getLocations().stream().collect(toMap(Location::getId, identity()));
        Map<String, idc.storyalbum.matcher.freemarker.context.Character> charCache = new HashMap<>();

        Map<String, Location> locIdToLocation = profile.getLocations()
                .stream()
                .collect(toMap(Location::getId, identity()));
        Map<String, idc.storyalbum.matcher.freemarker.context.Location> locCache = new HashMap<>();

        Context ctx = new Context();
        ctx.setSubject(subjectName);
        for (AlbumPage albumPage : album.getPages()) {
            Page page = new Page();
            charsToCtxChars(charIdToChar, charCache, albumPage, page);
            itemsToCtxItems(albumPage, page);
            locToCtxLoc(locIdToLocation, locCache, albumPage, page);
            ctx.getPages().add(page);
        }
        return ctx;
    }

    private void itemsToCtxItems(AlbumPage albumPage, Page page) {
        page.getItems().addAll(albumPage.getImage().getItemIds().elementSet());
    }

    private void locToCtxLoc(Map<String, Location> locIdToLocation, Map<String, idc.storyalbum.matcher.freemarker.context.Location> locCache, AlbumPage albumPage, Page page) {
        String locId = albumPage.getImage().getLocationId();
        if (!locCache.containsKey(locId)) {
            Location location = locIdToLocation.get(locId);
            if (location != null) {
                idc.storyalbum.matcher.freemarker.context.Location ctxLoc = new idc.storyalbum.matcher.freemarker.context.Location();
                ctxLoc.setName(location.getName());
                ctxLoc.setId(locId);
                locCache.put(locId, ctxLoc);
                page.setLocation(ctxLoc);
            }
        } else {
            page.setLocation(locCache.get(locId));
        }
    }

    private void charsToCtxChars(Map<String, Character> charIdToChar, Map<String, idc.storyalbum.matcher.freemarker.context.Character> charCache, AlbumPage albumPage, Page page) {
        for (String charId : albumPage.getImage().getCharacterIds()) {
            if (!charCache.containsKey(charId)) {
                Character character = charIdToChar.get(charId);
                if (character != null) {
                    idc.storyalbum.matcher.freemarker.context.Character ctxChar = new idc.storyalbum.matcher.freemarker.context.Character();
                    ctxChar.setName(character.getName());
                    ctxChar.setId(character.getId());
                    ctxChar.setGender(character.getGender());
                    ctxChar.getGroups().addAll(character.getGroups());
                    charCache.put(charId, ctxChar);
                    page.getCharacters().add(ctxChar);
                }
            } else {
                page.getCharacters().add(charCache.get(charId));
            }
        }
    }

    public void resolveText(Album album, Profile profile) throws TemplateErrorException {
        log.info("Starting to compile texts for the album");
        Context ctx = createContext(album, profile);
        if (log.isDebugEnabled()) {
            log.debug("  Context:");
            int i = 0;
            for (Page page : ctx.getPages()) {
                i++;
                log.debug("  Page {}", i);
                log.debug("    Location: {}", page.getLocation());
                for (idc.storyalbum.matcher.freemarker.context.Character character : page.getCharacters()) {
                    log.debug("    Character: {}", character);
                }
            }
        }
        int i = 0;
        for (AlbumPage albumPage : album.getPages()) {
            String template = albumPage.getStoryEvent().getText();
            try {
                log.debug("  Processing page {}, text {}", (i+1), template);
                ctx.setPage(ctx.getPages().get(i));
                String text = freeMarkerTemplate.process(template, ctx);
                log.debug("  Compiled text: {}", text);
                albumPage.setText(text);
                i++;
            } catch (TemplateException e) {
                log.error("Error processing template {}",template);
                log.error("Page is {}",albumPage);
                log.debug("Error while processing template", e);
                throw new TemplateErrorException("Error parsing template for page " + i + ": " + e.getMessage());
            }
        }
        log.info("Album text were successfully compiled");

    }
}
