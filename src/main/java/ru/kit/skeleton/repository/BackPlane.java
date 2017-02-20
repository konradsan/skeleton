package ru.kit.skeleton.repository;

import ru.kit.skeleton.model.Step;

/**
 * Created by mikha on 27.01.2017.
 */
public class BackPlane extends Plane {
    {
        getStepList().add(new Step("Мочка левого уха", ""));
        getStepList().add(new Step("Мочка правого уха", ""));
        getStepList().add(new Step("Наивысшая точка подмышки слева", ""));
        getStepList().add(new Step("Наивысшая точка подмышки справа", ""));
        getStepList().add(new Step("Левое плечо", ""));
        getStepList().add(new Step("Правое плечо", ""));
        getStepList().add(new Step("Изгиб талии слева", ""));
        getStepList().add(new Step("Изгиб талии справа", ""));
        getStepList().add(new Step("Край подвздошной кости слева", ""));
        getStepList().add(new Step("Край подвздошной кости справа", ""));
        getStepList().add(new Step("Центр пятки слева", ""));
        getStepList().add(new Step("Центр пятки справа", ""));
    }
}
