package com.cherkavi.productconfig.cd.statusholder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.cherkavi.productconfig.cd.statusholder.domain.DeployStatus;
import com.cherkavi.productconfig.cd.statusholder.domain.Status;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RestJdbcStatusApplicationTests {

	@LocalServerPort
	int portNumber;

	private String getUrl(){
		return String.format("http://localhost:%d/status", portNumber);
	}

	private static String MY_BRANCH = "personal_key";
    private static String MY_BRANCH2 = "personal_key2";

	@Test
	public void test_01createRecord() {
		// given
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DeployStatus> request = new HttpEntity<>(new DeployStatus(Status.NEW, MY_BRANCH), headers);
        RestTemplate restTemplate = new RestTemplateBuilder().build();

		// when
		DeployStatus response = restTemplate.postForObject(
				getUrl(),
				request,
				DeployStatus.class
		);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getId());
		Assert.assertEquals(1, response.getId());
	}

    @Test
    public void test_02createSecondRecord() {
        // given
        HttpEntity<DeployStatus> request = new HttpEntity<>(new DeployStatus(Status.NEW, MY_BRANCH2));
        RestTemplate restTemplate = new RestTemplateBuilder().build();

        // when
        DeployStatus response = restTemplate.postForObject(
                getUrl(),
                request,
                DeployStatus.class
        );

        // then
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getId());
        Assert.assertEquals(2, response.getId());
    }

    @Test
    public void test_03getAll() throws URISyntaxException {
        // given
        HttpEntity<DeployStatus> request = new HttpEntity<>(new DeployStatus(Status.NEW, MY_BRANCH2));
        RestTemplate restTemplate = new RestTemplateBuilder().build();

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                new RequestEntity<String>(HttpMethod.GET, new URI(getUrl() + "/search/findBy")),
                String.class);

        // then
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(2, JSONPath.eval(JSON.parseObject(response.getBody()),"/page/totalElements"));
    }

    @Test
    public void test_04getById() throws URISyntaxException {
        // given
        RestTemplate restTemplate = new RestTemplateBuilder().build();

        // when
        DeployStatus result = restTemplate.getForObject(getUrl() + "/1", DeployStatus.class);

        // then
        Assert.assertNotNull(result);
        Assert.assertEquals(MY_BRANCH, result.getBranch());
        Assert.assertEquals(Status.NEW, result.getStatus());
    }

    @Test
    public void test_06update() throws URISyntaxException {
        // given
        int portNumber = 1001;
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        DeployStatus existingObject = restTemplate.getForObject(getUrl() + "/1", DeployStatus.class);
        existingObject.setStatus(Status.DEPLOYING);
        existingObject.setPort(portNumber);

        // when
        restTemplate.put(getUrl() + "/1", existingObject);
        DeployStatus result = restTemplate.getForObject(getUrl() + "/1", DeployStatus.class);

        // then
        Assert.assertNotNull(result);
        Assert.assertEquals(MY_BRANCH, result.getBranch());
        Assert.assertEquals(Status.DEPLOYING, result.getStatus());
        Assert.assertEquals(new Integer(portNumber), result.getPort());
    }

    @Test
    public void test_07getByKey() throws URISyntaxException {
        // given
        RestTemplate restTemplate = new RestTemplateBuilder().build();

        // when
        ResponseEntity<DeployStatus[]> result = restTemplate.getForEntity(getUrl() + "/search/findByBranch?branch="+ MY_BRANCH, DeployStatus[].class);

        // then
        Assert.assertNotNull(result);
        DeployStatus[] records = result.getBody();
        Assert.assertEquals(1, records.length);
        Assert.assertEquals(MY_BRANCH, records[0].getBranch());
        Assert.assertEquals(Status.DEPLOYING, records[0].getStatus());
    }

    @Test(expected = HttpClientErrorException.class)
    public void test_08deleteByKey() throws URISyntaxException {
        // given
        RestTemplate restTemplate = new RestTemplateBuilder().build();

        // when
        restTemplate.delete(new URI(getUrl()+"/1"));

        // then
        restTemplate.getForEntity(getUrl() + "/1", DeployStatus.class);
    }

    @Test
    public void test_09getByKeyEmpty() throws URISyntaxException {
        // given
        RestTemplate restTemplate = new RestTemplateBuilder().build();

        // when
        ResponseEntity<DeployStatus[]> result = restTemplate.getForEntity(getUrl() + "/search/findByBranch?branch="+ MY_BRANCH, DeployStatus[].class);

        // then
        Assert.assertNotNull(result);
        DeployStatus[] records = result.getBody();
        Assert.assertEquals(0, records.length);
    }

}
