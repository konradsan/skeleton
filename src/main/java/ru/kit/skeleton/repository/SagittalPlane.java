package ru.kit.skeleton.repository;

import ru.kit.skeleton.model.Step;

/**
 * Created by mikha on 27.01.2017.
 */
public class SagittalPlane extends Plane {

    {
        getStepList().add(new Step("Пятка", ""));
        getStepList().add(new Step("Носок", ""));
        getStepList().add(new Step("Поясничный лордоз", "Изгиб в поясничном отделе позвоночника"));
        getStepList().add(new Step("Грудной кифоз", "Изгиб в грудном отделе позвоночника"));
        getStepList().add(new Step("Шейный лордоз", "Изгиб в шейном отделе позвоночника"));
        getStepList().add(new Step("Наивысшая точка на голове", ""));
    }
}
