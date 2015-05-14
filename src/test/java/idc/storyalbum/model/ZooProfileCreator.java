package idc.storyalbum.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Splitter;
import idc.storyalbum.matcher.model.profile.Character;
import idc.storyalbum.matcher.model.profile.Group;
import idc.storyalbum.matcher.model.profile.Profile;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Created by yonatan on 3/5/2015.
 */
public class ZooProfileCreator {

    private Set<String> readGroup(String resource) throws Exception {
        InputStream allIS = getClass().getResourceAsStream(resource);
        List<String> allLines = IOUtils.readLines(allIS);
        Set<String> collect = allLines.stream()
                .filter((x) -> !x.trim().isEmpty())
                .map((x) -> StringUtils.substringBefore(x, "(").toLowerCase())
                .map(String::trim)
                .collect(toSet());
        return collect;
    }

    private void print(String t, Set<String> c) {
        System.out.println("Type: " + t);
        for (String s : c) {
            System.out.println("  " + s);
        }
    }

    private Group createGroup(String id, String name) {
        Group g = new Group();
        g.setId(id);
        g.setName(name);
        return g;
    }

    @Test
    public void t() throws Exception {
        Profile profile = new Profile();
        InputStream allIS = getClass().getResourceAsStream("./all-animals.txt");
        List<String> allLines = IOUtils.readLines(allIS);
        Set<String> allAnimals;
        allAnimals = allLines.stream()
                .map((x) -> StringUtils.substringBefore(x, " : "))
                .collect(toSet());
        Set<String> horn = readGroup("horn.txt");
        Set<String> apes = readGroup("apes.txt");
        Set<String> fabs = readGroup("faboulas.txt");
        Set<String> teeth = readGroup("teeth.txt");
        Set<String> salmon = readGroup("salmon.txt");

        print("horns", horn);
        print("apes", apes);
        print("fabs", fabs);
        print("teeth", teeth);
        print("salmon", salmon);

        profile.getGroups().add(createGroup("animals", "Animals"));
        profile.getGroups().add(createGroup("monkeys", "Monkeys"));
        profile.getGroups().add(createGroup("with-horns", "With Horns"));
        profile.getGroups().add(createGroup("animals", "Animals"));
        profile.getGroups().add(createGroup("fabulous", "Fabulous"));
        profile.getGroups().add(createGroup("with-teeth", "With Sharp Teeth"));
        profile.getGroups().add(createGroup("eat-salmon", "Eats Salmon"));

        profile.setId("zoo");
        profile.setName("Zoo");
        for (String animal : allAnimals) {
            idc.storyalbum.matcher.model.profile.Character c = new Character();
            String id = StringUtils.replaceChars(animal, ' ', '-');
            String name = Splitter.on('-').splitToList(id).stream()
                    .map((x) -> StringUtils.capitalize(x))
                    .reduce("", (s, s2) -> s + " " + s2);
//            String name = StringUtils.capitalize(animal);
            c.setId(id);
            c.setName(name);
            c.getGroups().add("animals");
            c.setGender("");
            if (horn.contains(animal)) {
                c.getGroups().add("with-horns");
            }
            if (apes.contains(animal)) {
                c.getGroups().add("monkeys");
            }
            if (fabs.contains(animal)) {
                c.getGroups().add("fabulous");
            }
            if (teeth.contains(animal)) {
                c.getGroups().add("with-teeth");
            }
            if (salmon.contains(animal)) {
                c.getGroups().add("eat-salmon");
            }
            profile.getCharacters().add(c);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String profileStr = objectMapper.writeValueAsString(profile);
        System.out.println();
        System.out.println();
        System.out.println(profileStr);

    }
}
