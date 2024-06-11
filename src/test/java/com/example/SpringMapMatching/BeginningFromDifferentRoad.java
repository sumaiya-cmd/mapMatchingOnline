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
public class BeginningFromDifferentRoad {

    @Autowired
    MockMvc mockMVC;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void observationsOnDifferentRoads() throws Exception{
        List<Double[]> input = new ArrayList<>();
        input.add(new Double[] {76.66664779186249, 12.388243880225549});
        input.add(new Double[] {76.66661828756334, 12.388495376806313});
        input.add(new Double[] {76.66665583848955, 12.388825465700585});
        input.add(new Double[] {76.66665315628053, 12.389202709639692});
        input.add(new Double[] {76.6668274998665, 12.389558995081188});
        input.add(new Double[] {76.66689187288286, 12.389674263796302});
        input.add(new Double[] {76.66677922010423, 12.390025309114868});
        input.add(new Double[] {76.66692942380907, 12.39035015660068});
        input.add(new Double[] {76.66687577962875, 12.390756215388961});
        input.add(new Double[] {76.66696697473527, 12.39113607626369});
        input.add(new Double[] {76.66692942380907, 12.391560471896916});
        input.add(new Double[] {76.66703402996065, 12.39194295132033});
        input.add(new Double[] {76.6670796275139, 12.392524528546662});
        input.add(new Double[] {76.66725397109987, 12.392831034941285});
        input.add(new Double[] {76.66743904352188, 12.39341261018687});


        List<Double[]> actual = new ArrayList<>();

        for(Double[] coord : input){
            String json = String.format("[%f, %f]", coord[0], coord[1]);

            MvcResult result = mockMVC.perform(
                    post("/processCoordinates")
                            .content(json.getBytes())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andReturn();

            String jsonResponse = result.getResponse().getContentAsString();
            List<Point> apiResult = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
            if (apiResult != null) {
                for(int i = actual.size(); i < apiResult.size(); i++){
                    Double[] pointAsArray = new Double[]{apiResult.get(i).getLongitude(), apiResult.get(i).getLatitude()};
                    actual.add(pointAsArray);
                }
            }
        }

        List<Double[]> expected = new ArrayList<>();
        expected.add(new Double[] {76.66680259077864, 12.388332184461822});
        expected.add(new Double[] {76.66680843549703, 12.388544348848919});
        expected.add(new Double[] {76.66681428024758, 12.388756513231515});
        expected.add(new Double[] {76.66682596984512, 12.389180841983205});
        expected.add(new Double[] {76.66683765957127, 12.389605170716885});
        expected.add(new Double[] {76.66683765957127, 12.389605170716885});
        expected.add(new Double[] {76.66684934942604, 12.390029499432563});
        expected.add(new Double[] {76.6668634958757, 12.390247258839533});
        expected.add(new Double[] {76.666891788917, 12.39068277763225});
        expected.add(new Double[] {76.66692008214758, 12.391118296396657});
        expected.add(new Double[] {76.66694837556746, 12.39155381513275});
        expected.add(new Double[] {76.66697666917666, 12.391989333840536});
        expected.add(new Double[] {76.66713830945393, 12.392608431246826});
        expected.add(new Double[] {76.66719218975612, 12.392814796999845});
        expected.add(new Double[] {76.66739025134066, 12.393403668821545});


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


