package server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Activity;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.ActivityRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TestActivityRepository implements ActivityRepository {

    public List<Activity> activityList;
    public final List<String> calledMethods = new ArrayList<>();
    public final ObjectMapper objectMapper;

    public String json2 = "[\n" +
            "    {\n" +
            "        \"id\": \"0\",\n" +
            "        \"image_path\": \"../profile_images/picture-maarten.jpg\",\n" +
            "        \"title\": \"Taking a hot shower for 6 minutes\",\n" +
            "        \"consumption_in_wh\": 4000,\n" +
            "        \"source\": \"https://www.quora.com/How-can-I-estimate-the-kWh-of-electricity-when-I-take-a-shower\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"0\",\n" +
            "        \"image_path\": \"../profile_images/picture-maarten.jpg\",\n" +
            "        \"title\": \"Charging your smartphone at night\",\n" +
            "        \"consumption_in_wh\": 10,\n" +
            "        \"source\": \"https://9to5mac.com/2021/09/16/iphone-13-battery-life/\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"0\",\n" +
            "        \"image_path\": \"../profile_images/picture-maarten.jpg\",\n" +
            "        \"title\": \"other questions follow in a similar way\",\n" +
            "        \"consumption_in_wh\": 42,\n" +
            "        \"source\": \"https://cse1105.pages.ewi.tudelft.nl/2021-2022/course-website\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"0\",\n" +
            "        \"image_path\": \"../profile_images/picture-maarten.jpg\",\n" +
            "        \"title\": \"kek\",\n" +
            "        \"consumption_in_wh\": 43,\n" +
            "        \"source\": \"https://cse1105.pages.ewi.tudelft.nl/2021-2022/course-website\"\n" +
            "    }\n" +
            "]\n";

    public TestActivityRepository() throws JsonProcessingException {

        this.objectMapper = new ObjectMapper();
        this.activityList = objectMapper.readValue(json2, new TypeReference<List<Activity>>(){});

    }

    private void call(String name) {
        calledMethods.add(name);
    }

    @Override
    public List<Activity> findAll() {
        call("findAll");
        return activityList;
    }

    @Override
    public List<Activity> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Activity> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<Activity> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return activityList.size();
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Activity entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Activity> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends Activity> S save(S entity) {
        call("save");
        activityList.add(entity);
        return entity;
    }

    @Override
    public <S extends Activity> List<S> saveAll(Iterable<S> entities) {
        call("saveAll");
        List<S> list = new ArrayList<>();
        entities.forEach(e -> list.add(e));
        activityList.addAll(list);
        return list;
    }

    @Override
    public Optional<Activity> findById(Long aLong) {
        return Optional.of(activityList.get((int) aLong.longValue()));
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Activity> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Activity> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Activity> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Activity getOne(Long aLong) {
        return null;
    }

    @Override
    public Activity getById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Activity> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Activity> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Activity> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Activity> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Activity> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Activity> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Activity, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
}
