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
class SpiceGarden_UTurns {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper; // Autowire ObjectMapper to handle JSON parsing
    @Test
    void mapMatchingTest() throws Exception {
        List<Double[]> input = new ArrayList<>();
        input.add(new Double[]{77.70930109400483, 12.957859449280361});
        input.add(new Double[]{77.70933327063867, 12.957713070432172});
        input.add(new Double[]{77.70939494252019, 12.957584988869433});
        input.add(new Double[]{77.70945125162946, 12.957425540301298});
        input.add(new Double[]{77.70950219796636, 12.957305300329946});
        input.add(new Double[]{77.70956118846173, 12.95715107941213});
        input.add(new Double[]{77.70974620410634, 12.95706482021306});
        input.add(new Double[]{77.70972207163095, 12.957143237668001});
        input.add(new Double[]{77.70968721361096, 12.957221655098248});
        input.add(new Double[]{77.70964699281863, 12.957313142068985});
        input.add(new Double[]{77.70960945341248, 12.957399401182029});
        input.add(new Double[]{77.70956655123405, 12.957475204620348});
        input.add(new Double[]{77.70954241875864, 12.9575562358565});

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
            if (apiResult != null) {
                for (int i = actual.size(); i < apiResult.size(); i++) {
                    Double[] pointAsArray = new Double[]{apiResult.get(i).getLongitude(), apiResult.get(i).getLatitude()};
                    actual.add(pointAsArray);
                }
            }
        }

        List<Double[]> expected = new ArrayList<>() ;
        expected.add(new Double[]{77.70931837965833, 12.957875921553352});
        expected.add(new Double[]{77.70936232437509, 12.957763017480005});
        expected.add(new Double[]{77.70944422134744, 12.957535262558125});
        expected.add(new Double[]{77.7094921610385, 12.957418465081531});
        expected.add(new Double[]{77.70952811580685, 12.957325027060747});
        expected.add(new Double[]{77.70960402031773, 12.957136204286812});
        expected.add(new Double[]{77.70963598011178, 12.957052499299948});
        expected.add(new Double[]{77.70963598011178, 12.957052499299948});
        expected.add(new Double[]{77.70966993739296, 12.956966847656417});
        expected.add(new Double[]{77.7096998996999, 12.95688898250036});
        expected.add(new Double[]{77.709709218645, 12.956862313157245});
        expected.add(new Double[]{77.70976914325888, 12.95688177945064});
        expected.add(new Double[]{77.70965167620193, 12.95716576465739});
        expected.add(new Double[]{77.70965167620193, 12.95716576465739});
        expected.add(new Double[]{77.70961394563852, 12.957272397247422});
        expected.add(new Double[]{77.70958187465965, 12.957358806381677});
        expected.add(new Double[]{77.7095403710399, 12.957454407942109});
        expected.add(new Double[]{77.7095007539483, 12.957566555879723});

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