package ru.kit.skeleton.repository;

import ru.kit.skeleton.model.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by mikha on 13.01.2017.
 */
public class SagittalStepRepositoryImpl implements ListRepository {

    private List<Step> stepList = new ArrayList<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        stepList.add(new Step("Пятка", ""));
        stepList.add(new Step("Носок", ""));
        stepList.add(new Step("Талия", ""));
        stepList.add(new Step("Выпуклая часть спины", ""));
        stepList.add(new Step("Шея", ""));
    }

    @Override
    public Step get(int i) {
        if (i < stepList.size()) {
            return stepList.get(i);
        }
        return null;
    }

    @Override
    public int size() {
        return stepList.size();
    }

    @Override
    public Step getNext() {
        return get(counter.getAndIncrement());
    }

    @Override
    public Step getPrev() {
        return get(counter.decrementAndGet() - 1);
    }

    @Override
    public Step getThis() {
        return get(counter.get() - 1);
    }

    @Override
    public List<Step> getAllStepWhichPointNotNull() {
        return stepList.stream().filter(step -> step.getPoint() != null).collect(Collectors.toList());
    }

    @Override
    public void setDefault() {
        counter.set(0);
        stepList.stream().filter(step -> step.getPoint() != null).forEach(step -> step.setPoint(null));
    }

    @Override
    public Step getByName(String name) {
        return stepList.stream().filter(step -> step.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public boolean isFullPoint() {
        return getAllStepWhichPointNotNull().size() == stepList.size();
    }
}
