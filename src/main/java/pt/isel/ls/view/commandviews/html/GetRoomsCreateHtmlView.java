package pt.isel.ls.view.commandviews.html;

import pt.isel.ls.model.commands.common.CommandResult;
import pt.isel.ls.model.commands.results.GetRoomsCreateResult;
import pt.isel.ls.model.entities.Label;
import pt.isel.ls.utils.html.elements.Element;

import java.util.Iterator;

import static pt.isel.ls.utils.html.HtmlDsl.a;
import static pt.isel.ls.utils.html.HtmlDsl.body;
import static pt.isel.ls.utils.html.HtmlDsl.br;
import static pt.isel.ls.utils.html.HtmlDsl.div;
import static pt.isel.ls.utils.html.HtmlDsl.form;
import static pt.isel.ls.utils.html.HtmlDsl.h1;
import static pt.isel.ls.utils.html.HtmlDsl.head;
import static pt.isel.ls.utils.html.HtmlDsl.html;
import static pt.isel.ls.utils.html.HtmlDsl.input;
import static pt.isel.ls.utils.html.HtmlDsl.label;
import static pt.isel.ls.utils.html.HtmlDsl.p;
import static pt.isel.ls.utils.html.HtmlDsl.title;
import static pt.isel.ls.utils.html.elements.Input.InputType.CHECKBOX;
import static pt.isel.ls.utils.html.elements.Input.InputType.NUMBER;
import static pt.isel.ls.utils.html.elements.Input.InputType.SUBMIT;
import static pt.isel.ls.utils.html.elements.Input.InputType.TEXT;

public class GetRoomsCreateHtmlView extends HtmlView {

    private GetRoomsCreateResult result;

    public GetRoomsCreateHtmlView(CommandResult commandResult) {
        this.result = (GetRoomsCreateResult) commandResult;
    }

    @Override
    public String display() {
        return
                html(
                        head(
                                title("Room Creation")
                        ),
                        body(
                                a("/", "Home"),
                                h1("Create a Room"),
                                form("post", "/rooms/create",
                                        div(
                                                label("name", "Enter name: "),
                                                input(TEXT, "name", "New Room", "name")
                                        ),
                                        div(
                                                label("description", "Enter description: "),
                                                input(TEXT, "description", "", "description")
                                        ),
                                        div(
                                                label("location", "Enter location: "),
                                                input(TEXT, "location", "", "location")
                                        ),
                                        div(
                                                label("capacity", "Select minimum capacity: "),
                                                input(NUMBER, "capacity", "1", "capacity", "1")
                                        ),
                                        buildLabelCheckboxes(),
                                        input(SUBMIT, "submit", "Search")
                                )
                        )
                ).toString();
    }

    private Element buildLabelCheckboxes() {
        Element div = div();
        Iterator<Label> iter = result.getLabels().iterator();
        if (!iter.hasNext()) {
            div.addChild(p("No labels available."));
        } else {
            for (Label label : result.getLabels()) {
                String currLid = "lid" + label.getLid();
                div.addChild(input(CHECKBOX,
                        "label",
                        label.getName(),
                        currLid));
                div.addChild(label(currLid, label.getName()));
                div.addChild(br());
            }
        }
        return div;
    }
}
