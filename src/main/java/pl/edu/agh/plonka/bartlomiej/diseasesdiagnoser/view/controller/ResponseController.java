package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.controller;

import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Response;

public interface ResponseController<T> {
    Response<T> getResponse();
}
