package at.aau.softwaredynamics.diffws.rest;

import at.aau.softwaredynamics.diffws.domain.Matcher;
import at.aau.softwaredynamics.diffws.util.MatcherRegistry;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("v1/matchers")
public class MatcherController {
    private final MatcherRegistry matcherRegistry;

    public MatcherController() {
        this.matcherRegistry = new MatcherRegistry();
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET)
    public String getAll() throws JsonProcessingException {
        ArrayList<Matcher> allMatchers = new ArrayList<>();

        for (Map.Entry<Integer, Class<? extends com.github.gumtreediff.matchers.Matcher>> entry
                : matcherRegistry.getMatcherMap().entrySet()) {
            allMatchers.add(new Matcher(entry.getKey(), entry.getValue().getSimpleName() + "_Spoon"));
        }

        ObjectMapper mapper = new ObjectMapper();
        //Set JSON array name to Annotation in the Matcher class
        String rootName = Matcher.class.getAnnotation(JsonRootName.class).value();
        final ObjectWriter writer = mapper.writer().withRootName(rootName);
        return writer.writeValueAsString(allMatchers);
    }
}
