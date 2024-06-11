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
class WindowFollowedByIntersection {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper; // Autowire ObjectMapper to handle JSON parsing
    @Test
    void mapMatchingTest() throws Exception {
        List<Double[]> input = new ArrayList<>();
        input.add(new Double[] {76.72221511602403, 12.438265962662976});
        input.add(new Double[] {76.72256112098695, 12.438671946706007});
        input.add(new Double[] {76.72292053699495, 12.438881486608981});
        input.add(new Double[] {76.72316193580629, 12.439371285472845});
        input.add(new Double[] {76.72361791133882, 12.439737979204857});
        input.add(new Double[] {76.72401219606401, 12.440201584110758});
        input.add(new Double[] {76.72433942556383, 12.440591849745955});
        input.add(new Double[] {76.72487318515779, 12.440735907382578});
        input.add(new Double[] {76.72517359256746, 12.440890441849396});
        input.add(new Double[] {76.72574222087862, 12.441199510507166});
        input.add(new Double[] {76.7263698577881, 12.441288564119965});
        input.add(new Double[] {76.72719329595567, 12.441623824506012});
        input.add(new Double[] {76.72798722982407, 12.441746927820379});
        input.add(new Double[] {76.72913789749147, 12.442493404115133});
        input.add(new Double[] {76.72998011112215, 12.442899381550015});
        input.add(new Double[] {76.73014104366302, 12.44337607682465});
        input.add(new Double[] {76.73054069280626, 12.443527990519488});
        input.add(new Double[] {76.73079818487169, 12.44350179850949});
        input.add(new Double[] {76.7310717701912, 12.443858009619273});
        input.add(new Double[] {76.73126220703126, 12.443823960049023});
        input.add(new Double[] {76.73134267330171, 12.444057068556017});
        input.add(new Double[] {76.73151165246965, 12.444033495795164});
        input.add(new Double[] {76.73166722059251, 12.444159217161667});
        input.add(new Double[] {76.73173159360887, 12.44432422636285});
        input.add(new Double[] {76.73184692859651, 12.44442637486339});
        input.add(new Double[] {76.73200517892838, 12.444502331414668});
        input.add(new Double[] {76.73215270042421, 12.444504950605698});
        input.add(new Double[] {76.73223853111269, 12.444562572801614});
        input.add(new Double[] {76.73228949308397, 12.444745916067163});
        input.add(new Double[] {76.73243433237077, 12.444850683589348});
        input.add(new Double[] {76.73261135816576, 12.444840206839034});
        input.add(new Double[] {76.73263281583787, 12.444992119677233});
        input.add(new Double[] {76.73281252384187, 12.444968547001261});
        input.add(new Double[] {76.7329251766205, 12.445233084686537});
        input.add(new Double[] {76.73334896564485, 12.445120459764375});
        input.add(new Double[] {76.73332482576372, 12.445515956368611});



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
                apiResult.clear();
            }
        }

        List<Double[]> expected = new ArrayList<>() ;
        expected.add(new Double[] {76.72231575144811, 12.438308654483842});
        expected.add(new Double[] {76.72259260714596, 12.438634970490307});
        expected.add(new Double[] {76.7228694635525, 12.438961286114822});
        expected.add(new Double[] {76.72314632066771, 12.439287601357355});
        expected.add(new Double[] {76.72356160766938, 12.439777073504892});
        expected.add(new Double[] {76.72397689626574, 12.440266544792848});
        expected.add(new Double[] {76.72434818585963, 12.440489176630035});
        expected.add(new Double[] {76.72490512135556, 12.440823123266977});
        expected.add(new Double[] {76.72509076681558, 12.440934438514255});
        expected.add(new Double[] {76.72571570489824, 12.441141599782599});
        expected.add(new Double[] {76.7263406437977, 12.44134875947276});
        expected.add(new Double[] {76.72715625566723, 12.441577974327306});
        expected.add(new Double[] {76.72797186866006, 12.441807186528804});
        expected.add(new Double[] {76.72918079247373, 12.442396664169546});
        expected.add(new Double[] {76.72990599660973, 12.442927656847406});
        expected.add(new Double[] {76.73023386485363, 12.443167719422101});
        expected.add(new Double[] {76.73056173367624, 12.443407781515432});
        expected.add(new Double[] {76.73072566830454, 12.443527812381584});
        expected.add(new Double[] {76.73105049513218, 12.443765645890245});
        expected.add(new Double[] {76.73121138732617, 12.44388344853718});
        expected.add(new Double[] {76.73137227965952, 12.444001251068185});
        expected.add(new Double[] {76.73153317213223, 12.444119053483252});
        expected.add(new Double[] {76.7316940647443, 12.444236855782387});
        expected.add(new Double[] {76.7316940647443, 12.444236855782387});
        expected.add(new Double[] {76.73187045268511, 12.444369013886073});
        expected.add(new Double[] {76.73204684079789, 12.444501171849815});
        expected.add(new Double[] {76.73204684079789, 12.444501171849815});
        expected.add(new Double[] {76.73222322908265, 12.444633329673612});
        expected.add(new Double[] {76.7323996175394, 12.444765487357458});
        expected.add(new Double[] {76.7323996175394, 12.444765487357458});
        expected.add(new Double[] {76.73257600616813, 12.44489764490135});
        expected.add(new Double[] {76.73257600616813, 12.44489764490135});
        expected.add(new Double[] {76.73275239496886, 12.445029802305282});
        expected.add(new Double[] {76.73292878394155, 12.44516195956925});
        expected.add(new Double[] {76.73310517308624, 12.445294116693253});
        expected.add(new Double[] {76.73328156240292, 12.445426273677286});


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

