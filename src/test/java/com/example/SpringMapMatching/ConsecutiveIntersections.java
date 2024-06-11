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
class ConsecutiveIntersections {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper; // Autowire ObjectMapper to handle JSON parsing
    @Test
    void mapMatchingTest() throws Exception {
        List<Double[]> input = new ArrayList<>();
        input.add(new Double[] {76.69666707515717, 12.409588788963246});
        input.add(new Double[] {76.69695138931276, 12.409641179813228});
        input.add(new Double[] {76.69707745313646, 12.409952905152547});
        input.add(new Double[] {76.69736981391908, 12.41018866356468});
        input.add(new Double[] {76.69761121273042, 12.41030392315515});
        input.add(new Double[] {76.6979143023491, 12.41053182264976});
        input.add(new Double[] {76.69825226068498, 12.410754482882863});
        input.add(new Double[] {76.69852048158647, 12.411097641222181});
        input.add(new Double[] {76.69867604970932, 12.411456516253512});
        input.add(new Double[] {76.69901400804521, 12.411810151749377});
        input.add(new Double[] {76.69920444488527, 12.412203079514732});
        input.add(new Double[] {76.6995692253113, 12.412737460323866});
        input.add(new Double[] {76.69981598854066, 12.412967977196997});
        input.add(new Double[] {76.69989109039308, 12.41338185925237});
        input.add(new Double[] {76.70028269290925, 12.413939813133581});
        input.add(new Double[] {76.70034974813463, 12.414387747074112});
        input.add(new Double[] {76.70062601566316, 12.414626120962119});
        input.add(new Double[] {76.70062333345415, 12.414929981865477});
        input.add(new Double[] {76.70070648193361, 12.415315046466164});
        input.add(new Double[] {76.70096665620805, 12.415611048118857});
        input.add(new Double[] {76.70105516910554, 12.41600920910297});
        input.add(new Double[] {76.7011946439743, 12.41632092681672});
        input.add(new Double[] {76.70135557651521, 12.416622166269379});
        input.add(new Double[] {76.70157551765443, 12.416899830151673});
        input.add(new Double[] {76.70162111520767, 12.417232502526202});
        input.add(new Double[] {76.701857149601, 12.417669952954038});
        input.add(new Double[] {76.70215219259262, 12.418005243802089});
        input.add(new Double[] {76.70228630304338, 12.418398162214833});
        input.add(new Double[] {76.70251697301865, 12.418539612698188});
        input.add(new Double[] {76.70265376567842, 12.41882251343417});
        input.add(new Double[] {76.70298099517824, 12.419149944457542});
        input.add(new Double[] {76.70343160629272, 12.419485233398444});
        input.add(new Double[] {76.70378029346467, 12.419959352178958});



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
        expected.add(new Double[] {76.69665236162763, 12.409552039953814});
        expected.add(new Double[] {76.69697215768396, 12.409759013917437});
        expected.add(new Double[] {76.69713076557636, 12.409874616757342});
        expected.add(new Double[] {76.69744798176444, 12.410105822101247});
        expected.add(new Double[] {76.69760398646083, 12.410242069476583});
        expected.add(new Double[] {76.69791599633311, 12.4105145638906});
        expected.add(new Double[] {76.69822152664301, 12.410819685496389});
        expected.add(new Double[] {76.69852057750045, 12.41115743430184});
        expected.add(new Double[] {76.69872086259085, 12.411443965158849});
        expected.add(new Double[] {76.69899694040569, 12.411861086162979});
        expected.add(new Double[] {76.69928518798542, 12.412222528327211});
        expected.add(new Double[] {76.6995922723601, 12.4126831350247});
        expected.add(new Double[] {76.69977449642639, 12.413060636389588});
        expected.add(new Double[] {76.69995672107508, 12.413438137553497});
        expected.add(new Double[] {76.7002300591402, 12.414004388922503});
        expected.add(new Double[] {76.70041228524504, 12.414381889583924});
        expected.add(new Double[] {76.70050339851588, 12.41457063983925});
        expected.add(new Double[] {76.70068562549449, 12.414948140199138});
        expected.add(new Double[] {76.70079874322714, 12.41535252841419});
        expected.add(new Double[] {76.7009469892523, 12.41569839441372});
        expected.add(new Double[] {76.70111428510607, 12.416054903426948});
        expected.add(new Double[] {76.70120745792507, 12.416238479441683});
        expected.add(new Double[] {76.70139380399435, 12.41660563131683});
        expected.add(new Double[] {76.70158315207853, 12.416985320553618});
        expected.add(new Double[] {76.70167932707177, 12.417181433874372});
        expected.add(new Double[] {76.7018716775364, 12.417573660348989});
        expected.add(new Double[] {76.70215788955826, 12.418086675196985});
        expected.add(new Double[] {76.70234817787524, 12.418379858038964});
        expected.add(new Double[] {76.70244306200627, 12.41850203598959});
        expected.add(new Double[] {76.7026841830733, 12.41878513313804});
        expected.add(new Double[] {76.7029766573094, 12.419106971312708});
        expected.add(new Double[] {76.70339035297941, 12.419484650367387});
        expected.add(new Double[] {76.70383972902052, 12.419923093047636});


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