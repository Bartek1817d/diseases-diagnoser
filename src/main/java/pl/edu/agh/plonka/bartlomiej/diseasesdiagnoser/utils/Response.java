package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils;

public class Response<T> {

    public boolean okClicked;
    public T content;

    public Response(boolean okClicked, T content) {
        this.okClicked = okClicked;
        this.content = content;
    }

    public Response(boolean okClicked) {
        this.okClicked = okClicked;
    }
}
