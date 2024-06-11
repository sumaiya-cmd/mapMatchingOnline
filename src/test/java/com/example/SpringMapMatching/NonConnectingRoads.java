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
class NonConnectingRoads {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper; // Autowire ObjectMapper to handle JSON parsing
    @Test
    void mapMatchingTest() throws Exception {
        List<Double[]> input = new ArrayList<>();
        input.add(new Double[] {77.4075758457184, 12.815787203322058});
        input.add(new Double[] {77.40778505802156, 12.816059203792776});
        input.add(new Double[] {77.40803182125093, 12.816386127046762});
        input.add(new Double[] {77.40816593170167, 12.816548280823291});
        input.add(new Double[] {77.40832686424257, 12.81676274210932});
        input.add(new Double[] {77.4084582924843, 12.816919664885832});
        input.add(new Double[] {77.40852802991868, 12.81703212614885});
        input.add(new Double[] {77.40867286920549, 12.817230894769983});
        input.add(new Double[] {77.4087855219841, 12.817379971132866});
        input.add(new Double[] {77.40889817476274, 12.81754735501411});
        input.add(new Double[] {77.40902155637743, 12.817659815996821});
        input.add(new Double[] {77.40904033184053, 12.81783243043073});
        input.add(new Double[] {77.40894645452501, 12.817947506654285});
        input.add(new Double[] {77.40886330604555, 12.818114890158277});
        input.add(new Double[] {77.4088016152382, 12.818282273551027});
        input.add(new Double[] {77.40867286920549, 12.818339811566593});
        input.add(new Double[] {77.40894645452501, 12.818467964372179});
        input.add(new Double[] {77.40908592939378, 12.818703346906396});
        input.add(new Double[] {77.40928173065187, 12.81883411488591});
        input.add(new Double[] {77.40943998098375, 12.819111342777857});
        input.add(new Double[] {77.40965455770494, 12.819281340862512});
        input.add(new Double[] {77.40969747304918, 12.819472261651526});


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
                for (int i = actual.size(); i < apiResult.size(); i++) {
                    Double[] pointAsArray = new Double[]{apiResult.get(i).getLongitude(), apiResult.get(i).getLatitude()};
                    actual.add(pointAsArray);
                }
            }
        }

        List<Double[]> expected = new ArrayList<>() ;
        expected.add(new Double[] {77.40766643125221, 12.815809600189912});
        expected.add(new Double[] {77.4077964630839, 12.815987210284499});
        expected.add(new Double[] {77.40805652732828, 12.816342430176777});
        expected.add(new Double[] {77.40818655974098, 12.816520039974467});
        expected.add(new Double[] {77.40831659234736, 12.816697649673188});
        expected.add(new Double[] {77.40844662514742, 12.816875259272933});
        expected.add(new Double[] {77.40857665814114, 12.8170528687737});
        expected.add(new Double[] {77.40870669132858, 12.817230478175492});
        expected.add(new Double[] {77.4088367247097, 12.8174080874783});
        expected.add(new Double[] {77.4089667582845, 12.817585696682125});
        expected.add(new Double[] {77.40900394022773, 12.817665879502412});
        expected.add(new Double[] {77.4090092409678, 12.817864337426888});
        expected.add(new Double[] {77.4090092409678, 12.817864337426888});
        expected.add(new Double[] {77.40892249494537, 12.818052804743715});
        expected.add(new Double[] {77.4087580790227, 12.818298422407292});
        expected.add(new Double[] {77.4086549251088, 12.818383801104666});
        expected.add(new Double[] {77.4087580790227, 12.818298422407292});
        expected.add(new Double[] {77.4087580790227, 12.818298422407292});
        expected.add(new Double[] {77.4087580790227, 12.818298422407292});
        expected.add(new Double[] {77.4087580790227, 12.818298422407292});
        expected.add(new Double[] {77.4087580790227, 12.818298422407292});
        expected.add(new Double[] {77.4087580790227, 12.818298422407292});

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