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
public class SpiceGarden_Turns {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper; // Autowire ObjectMapper to handle JSON parsing
    @Test
    void mapMatchingTest() throws Exception {
        List<Double[]> input = new ArrayList<>();
        input.add(new Double[] {77.7101692557335, 12.957676475706688});
        input.add(new Double[] {77.7102228999138, 12.957663406160565});
        input.add(new Double[] {77.71027922630311, 12.957707842614582});
        input.add(new Double[] {77.71036237478258, 12.957692159161118});
        input.add(new Double[] {77.71039992570878, 12.957747051243881});
        input.add(new Double[] {77.71041601896287, 12.957825468483984});
        input.add(new Double[] {77.71032214164735, 12.957930024765698});
        input.add(new Double[] {77.71032214164735, 12.95806856177135});
        input.add(new Double[] {77.71023094654085, 12.95813390938878});
        input.add(new Double[] {77.71023899316789, 12.958290743600621});
        input.add(new Double[] {77.71012634038927, 12.958371774571363});
        input.add(new Double[] {77.71015852689744, 12.958458033317703});
        input.add(new Double[] {77.71008610725404, 12.958549519834191});
        input.add(new Double[] {77.7100968360901, 12.958664531407246});
        input.add(new Double[] {77.71003246307374, 12.958737720562425});

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
        expected.add(new Double[] {77.7101668553214, 12.957653008189837});
        expected.add(new Double[] {77.71024000307506, 12.957677104568248});
        expected.add(new Double[] {77.71031212057801, 12.957703208975573});
        expected.add(new Double[] {77.71036569358017, 12.957720277241037});
        expected.add(new Double[] {77.71040651485424, 12.957733653271688});
        expected.add(new Double[] {77.71038014182983, 12.957796180085893});
        expected.add(new Double[] {77.71034506331114, 12.957886670468014});
        expected.add(new Double[] {77.71026252562012, 12.958089770983747});
        expected.add(new Double[] {77.71023570087061, 12.958166184996259});
        expected.add(new Double[] {77.71020681267873, 12.958242598985365});
        expected.add(new Double[] {77.7101737976023, 12.958337110992062});
        expected.add(new Double[] {77.71014078252591, 12.958429612070205});
        expected.add(new Double[] {77.71010776744953, 12.958528145790922});
        expected.add(new Double[] {77.71007681581534, 12.958630701253412});
        expected.add(new Double[] {77.71003142008533, 12.958719180441989});

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
