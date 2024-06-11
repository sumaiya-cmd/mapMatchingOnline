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

//passed
@SpringBootTest
@AutoConfigureMockMvc
class WindowWithIntersectionTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper; // Autowire ObjectMapper to handle JSON parsing
    @Test
    void mapMatchingTest() throws Exception {
        List<Double[]> input = new ArrayList<>();
        input.add(new Double[] {76.66698038578033, 12.382179071555644});
        input.add(new Double[] {76.66676580905916, 12.382970255455009});
        input.add(new Double[] {76.6669535636902, 12.38370904090401});
        input.add(new Double[] {76.66665315628053, 12.384526418112515});
        input.add(new Double[] {76.66683018207551, 12.385611010840098});
        input.add(new Double[] {76.66673898696901, 12.38652269399294});
        input.add(new Double[] {76.66670680046083, 12.38728242751998});
        input.add(new Double[] {76.66676044464113, 12.388587068207736});
        input.add(new Double[] {76.66652977466585, 12.389278682311431});
        input.add(new Double[] {76.66663169860841, 12.389975534061143});
        input.add(new Double[] {76.6668623685837, 12.390756215388961});
        input.add(new Double[] {76.66687846183778, 12.39171503556708});
        input.add(new Double[] {76.66699647903444, 12.392831034941285});
        input.add(new Double[] {76.66738271713258, 12.393475483108706});
        input.add(new Double[] {76.66744709014894, 12.394156605456676});
        input.add(new Double[] {76.66775017976761, 12.394636009734663});
        input.add(new Double[] {76.66800230741502, 12.395102314689348});
        input.add(new Double[] {76.6679432988167, 12.39568650455109});
        input.add(new Double[] {76.66815519332887, 12.396315227603356});
        input.add(new Double[] {76.66845023632051, 12.396833922979884});
        input.add(new Double[] {76.66862994432451, 12.397397151639069});
        input.add(new Double[] {76.66865676641464, 12.397824156741667});


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
                for(int i = actual.size(); i < apiResult.size(); i++){
                    Double[] pointAsArray = new Double[]{apiResult.get(i).getLongitude(), apiResult.get(i).getLatitude()};
                    actual.add(pointAsArray);
                }
                apiResult.clear();
            }
        }

        List<Double[]> expected = new ArrayList<>();
        expected.add(new Double[] {76.66708422097928, 12.382257667020678});
        expected.add(new Double[] {76.66696447671778, 12.38304525220111});
        expected.add(new Double[] {76.6668467274875, 12.383770167971623});
        expected.add(new Double[] {76.66676383738434, 12.384508269789011});
        expected.add(new Double[] {76.66672215737488, 12.385572918194343});
        expected.add(new Double[] {76.66675471136455, 12.386594134780106});
        expected.add(new Double[] {76.66677291853166, 12.387255068690713});
        expected.add(new Double[] {76.66668682018772, 12.38854701148048});
        expected.add(new Double[] {76.66671924421227, 12.389356075251966});
        expected.add(new Double[] {76.66674403707343, 12.389976067726797});
        expected.add(new Double[] {76.66677303271486, 12.390819832787411});
        expected.add(new Double[] {76.6668004093818, 12.391634351494844});
        expected.add(new Double[] {76.6670832465601, 12.392828485247698});
        expected.add(new Double[] {76.66727245519023, 12.393407619362504});
        expected.add(new Double[] {76.66754538325375, 12.394238268832957});
        expected.add(new Double[] {76.667675666581, 12.394638084984816});
        expected.add(new Double[] {76.66787109245274, 12.39523780897893});
        expected.add(new Double[] {76.66799985927427, 12.395638132378721});
        expected.add(new Double[] {76.66819073385598, 12.396239378589788});
        expected.add(new Double[] {76.66837924465997, 12.396827434736082});
        expected.add(new Double[] {76.66856302679493, 12.397389111040738});
        expected.add(new Double[] {76.6687214990525, 12.397776788990102});

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

