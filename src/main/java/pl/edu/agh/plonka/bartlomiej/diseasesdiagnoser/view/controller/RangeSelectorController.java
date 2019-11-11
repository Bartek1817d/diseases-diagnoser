package pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.view.controller;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import org.controlsfx.control.RangeSlider;
import pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.Response;

import static com.google.common.collect.BoundType.CLOSED;
import static com.google.common.collect.BoundType.OPEN;
import static com.google.common.collect.Range.range;
import static java.lang.Math.round;
import static pl.edu.agh.plonka.bartlomiej.diseasesdiagnoser.utils.binding.ObservableResourceFactory.getStringBinding;

public class RangeSelectorController implements ResponseController<Range<Integer>> {

    @FXML
    private RangeSlider rangeSlider;
    @FXML
    private CheckBox leftInclusiveCheckBox;
    @FXML
    private CheckBox rightInclusiveCheckBox;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    private Stage dialogStage;
    private boolean okClicked = false;

    public void init(Stage dialogStage, Integer minValue, Integer maxValue, Range<Integer> range) {
        this.dialogStage = dialogStage;
        this.initRangeSlider(minValue, maxValue, range);
    }

    private void initRangeSlider(Integer minValue, Integer maxValue, Range<Integer> range) {
        rangeSlider.setMin(minValue);
        rangeSlider.setMax(maxValue);
        if (range.hasLowerBound()) {
            rangeSlider.setLowValue(range.lowerEndpoint());
            leftInclusiveCheckBox.setSelected(range.lowerBoundType() == CLOSED);
        } else {
            rangeSlider.setLowValue(minValue);
        }
        if (range.hasUpperBound()) {
            rangeSlider.setHighValue(range.upperEndpoint());
            rightInclusiveCheckBox.setSelected(range.upperBoundType() == CLOSED);
        } else {
            rangeSlider.setHighValue(maxValue);
        }
    }

    @FXML
    private void initialize() {
        bindTranslations();
    }

    private void bindTranslations() {
        leftInclusiveCheckBox.textProperty().bind(getStringBinding("LEFT_INCLUSIVE"));
        rightInclusiveCheckBox.textProperty().bind(getStringBinding("RIGHT_INCLUSIVE"));
        okButton.textProperty().bind(getStringBinding("OK"));
        cancelButton.textProperty().bind(getStringBinding("CANCEL"));
    }

    public void setMinValue(Integer minValue) {
        rangeSlider.setMin(minValue);
    }

    public void setMaxValue(Integer maxValue) {
        rangeSlider.setMax(maxValue);
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @Override
    public Response<Range<Integer>> getResponse() {
        Range<Integer> range = convertToRange();
        return new Response<>(okClicked, range);
    }

    @FXML
    private void handleOK() {
        okClicked = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    public Range<Integer> convertToRange() {
        BoundType lowerType, upperType;
        if (leftInclusiveCheckBox.isSelected())
            lowerType = CLOSED;
        else
            lowerType = OPEN;

        if (rightInclusiveCheckBox.isSelected())
            upperType = CLOSED;
        else
            upperType = OPEN;

        return range((int) round(rangeSlider.getLowValue()), lowerType, (int) round(rangeSlider.getHighValue()), upperType);
    }

}
