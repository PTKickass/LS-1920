package pt.isel.ls.model.commands.results;

import pt.isel.ls.model.commands.common.CommandResult;
import pt.isel.ls.model.commands.common.ValidatedResult;
import pt.isel.ls.model.entities.Label;

import java.util.LinkedList;
import java.util.List;

public class GetRoomsCreateResult extends ValidatedResult implements CommandResult {

    private LinkedList<Label> labels;

    private String previousName = "";
    private String previousDescription = "";
    private String previousLocation = "";
    private String previousCapacity = "";
    private List<String> previousLabels;

    public void addLabel(Label label) {
        if (labels == null) {
            labels = new LinkedList<>();
        }
        labels.add(label);
    }

    public void setPreviousName(String previousName) {
        if (previousName != null) {
            this.previousName = previousName;
        }
    }

    public void setPreviousDescription(String previousDescription) {
        if (previousDescription != null) {
            this.previousDescription = previousDescription;
        }
    }

    public void setPreviousLocation(String previousLocation) {
        if (previousLocation != null) {
            this.previousLocation = previousLocation;
        }
    }

    public void setPreviousCapacity(String previousCapacity) {
        if (previousCapacity != null) {
            this.previousCapacity = previousCapacity;
        }
    }

    public void setPreviousLabels(List<String> previousLabels) {
        this.previousLabels = previousLabels;
    }

    public Iterable<Label> getLabels() {
        return labels;
    }

    public String getPreviousName() {
        return previousName;
    }

    public String getPreviousDescription() {
        return previousDescription;
    }

    public String getPreviousLocation() {
        return previousLocation;
    }

    public String getPreviousCapacity() {
        return previousCapacity;
    }

    public List<String> getPreviousLabels() {
        return previousLabels;
    }

    @Override
    public boolean hasResults() {
        return true;
    }

    @Override
    public ResultType getResultType() {
        return ResultType.GetRoomsCreate;
    }
}
