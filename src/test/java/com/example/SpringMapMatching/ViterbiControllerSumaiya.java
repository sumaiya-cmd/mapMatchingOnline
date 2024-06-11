package com.example.SpringMapMatching;
import com.example.SpringMapMatching.Model.Point;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.ArrayList;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
class ViterbiControllerSumaiya {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper; // Autowire ObjectMapper to handle JSON parsing
    @Test
    void mapMatchingTest() throws Exception {
        List<Double[]> input = new ArrayList<>();
        input.add(new Double[] {76.73391720314538, 12.446058126231062});
        input.add(new Double[] {76.73439478311016, 12.446351473703901});
        input.add(new Double[] {76.73447527411547, 12.44676006427438});
        input.add(new Double[] {76.73467918466218, 12.447053410954016});
        input.add(new Double[] {76.73487772914193, 12.447200084169511});
        input.add(new Double[] {76.73516749676098, 12.447194845841814});
        input.add(new Double[] {76.73524262169924, 12.447561528525355});
        input.add(new Double[] {76.73541433584387, 12.447812967780427});
        input.add(new Double[] {76.73560751425663, 12.447954402254359});
        input.add(new Double[] {76.73577922840124, 12.44807488341211});
        input.add(new Double[] {76.73602606748413, 12.448205841128846});
        input.add(new Double[] {76.7361011924224, 12.448499186174539});
        input.add(new Double[] {76.73631046903621, 12.44869824155246});
        input.add(new Double[] {76.73651974564996, 12.448792530888692});
        input.add(new Double[] {76.73671343195156, 12.449232547338289});

        List<Double[]> actual = new ArrayList<>(); // List to store converted points
        for (Double[] coord : input) {
            String json = String.format("[%f, %f]", coord[0], coord[1]);
            MvcResult mvcResult = mockMvc.perform(
                            post("/processCoordinates")
                                    .content(json.getBytes())
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andReturn();
            String jsonResponse = mvcResult.getResponse().getContentAsString();
            System.out.println(jsonResponse);
            // Parse the response to List<Point>
            List<Point> apiResult = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
            if (apiResult != null && apiResult.size() >= 15) {
                for (Point point : apiResult) {
                    Double[] pointAsArray = new Double[]{point.getLongitude(), point.getLatitude()};
                    actual.add(pointAsArray);
                }
            }
        }

        List<Double[]> expected = new ArrayList<>() ;
        expected.add(new Double[] {76.73398712138955, 12.445954900213648});
        expected.add(new Double[] {76.73441798957599, 12.446453917208165});
        expected.add(new Double[] {76.73456161267902, 12.446620256001331});
        expected.add(new Double[] {76.73484885944644, 12.446952933280139});
        expected.add(new Double[] {76.73499248311084, 12.447119271765773});
        expected.add(new Double[] {76.73513610696236, 12.447285610148885});
        expected.add(new Double[] {76.735279731001, 12.447451948429482});
        expected.add(new Double[] {76.73556697963969, 12.447784624683097});
        expected.add(new Double[] {76.73571060423974, 12.447950962656112});
        expected.add(new Double[] {76.73585422902694, 12.448117300526592});
        expected.add(new Double[] {76.73599785400127, 12.448283638294535});
        expected.add(new Double[] {76.73614147916274, 12.44844997595994});
        expected.add(new Double[] {76.73627924751715, 12.448606628961745});
        expected.add(new Double[] {76.7364170160402, 12.448763281869665});
        expected.add(new Double[] {76.7368303226213, 12.449233240030111});
        Assertions.assertEquals(convert(expected) , convert(actual));
    }
    List<List<Double>> convert(List<Double[]> coords){
        List<List<Double>> resultList = new ArrayList<>();
        for (Double[] array : coords) {
            List<Double> list = new ArrayList<>();
            for (Double value : array) {
                list.add(value);
            }
            resultList.add(list);
        }
        return resultList;
    }
}