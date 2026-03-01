package Screens;

import Components.CustomAlert;
import Components.ScreenManager;
import Controllers.AuthenticationController;
import Controllers.FlashcardController;
import Controllers.SettingsController;
import RecordClasses.RectangleButtonBundle;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

import java.io.IOException;

public class RevisionSystemScreen extends SystemScreens{
    private final AuthenticationController ac;
    private final SettingsController sc;
    private final FlashcardController fc;

    public RevisionSystemScreen(AuthenticationController ac, SettingsController sC) {
        this.ac = ac;
        this.sc = sC;
        this.fc = new FlashcardController(ac.getCurrentUser().getEmail());
    }

    public Scene create(){
        Pane root = new Pane();
        root.getStyleClass().add("screens");
        Scene scene = new Scene(root, 1920, 1057);
        Group background = createBackground("Revision System");
        getTitle().setLayoutX(275);
        ImageView back = getSwitchScreenBackView(ac);

        Group contentLayer = new Group();

        RectangleButtonBundle fcManagement = ui.buildRectangularButtonBundle(rectW, rectH, "Flashcards", "buttonText", 310, 250);
        ImageView fcmIcon = createIcon(310, 245, "/images/RevisionSystemScreen/flashcardmanagement.png");
        fcManagement.button().setOnMouseClicked(_ -> {
            try {
                fcManagementMouseClick();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        RectangleButtonBundle studyModes = ui.buildRectangularButtonBundle(rectW, rectH, "Study Modes", "buttonText", 310,  420);
        ImageView smIcon = createIcon(310, 415, "/images/RevisionSystemScreen/studymodes.png");
        studyModes.button().setOnMouseClicked(_ -> {
            try {
               smMouseClick();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        RectangleButtonBundle impOrExp = ui.buildRectangularButtonBundle(rectW, rectH, "Import / Export", "buttonText", 310, 591);
        ImageView impIcon = createIcon(310, 586, "/images/RevisionSystemScreen/import.png");
        impOrExp.button().setOnMouseClicked(_ -> buildImportExport(contentLayer));
        RectangleButtonBundle analytics = ui.buildRectangularButtonBundle(rectW, rectH, "Analytics", "buttonText", 310, 762);
        ImageView aIcon = createIcon(310, 757, "/images/RevisionSystemScreen/analytics.png");

        Group buttons = new Group(fcManagement.button(), studyModes.button(), impOrExp.button(), analytics.button(), fcmIcon, smIcon, impIcon, aIcon);
        contentLayer.getChildren().add(buttons);
        root.getChildren().addAll(background, back, contentLayer);
        return scene;
    }

    // Procedure switching the scene the FMS
    private void fcManagementMouseClick() throws IOException {
        ScreenManager.switchScene(new FlashcardManagementScreen(ac, sc, fc).create(), ac.getCurrentUser().getEmail());
    }

    // Procedure creating the Import / Export Content
    private void buildImportExport(Group contentLayer){

        Window owner = contentLayer.getScene().getWindow();
        RectangleButtonBundle importCSV = ui.buildRectangularButtonBundle(rectW, rectH, "Import CSV", "buttonText", 293, 315);
        ImageView importIcon = createIcon(297, 310, "/images/RevisionSystemScreen/upload.png");
        importCSV.button().setOnMouseClicked(_ -> {
            try {
                if(fc.importCSV(owner)) CustomAlert.success(owner, "Successfully imported CSV file");
            } catch (IOException e) {
                CustomAlert.error(owner, "Could not import, try again later");
                throw new RuntimeException(e);
            }
        });

        RectangleButtonBundle exportCSV = ui.buildRectangularButtonBundle(rectW, rectH, "Export CSV", "buttonText", 293, 485);
        ImageView exportIcon = createIcon(293, 480, "/images/RevisionSystemScreen/textFile.png");
        exportCSV.button().setOnMouseClicked(_ -> {
            try {
                boolean exported = fc.export(owner, false);
                if(exported) CustomAlert.success(owner, "Successfully exported CSV file");
                if(!exported) CustomAlert.error(owner, "Could not export CSV file");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        RectangleButtonBundle exportHTML = ui.buildRectangularButtonBundle(rectW, rectH, "Export HTML", "buttonText", 293, 656);
        ImageView htmlIcon = createIcon(293, 651, "/images/RevisionSystemScreen/file.png");
        exportHTML.button().setOnMouseClicked(_ -> {
            try {
                boolean exported = fc.export(owner, true);
                if(exported) CustomAlert.success(owner, "Successfully exported CSV file as HTML \n You can print this to a PDF");
                if(!exported) CustomAlert.error(owner, "Could not export CSV file");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ImageView hintView = ui.getHintView();
        String importInfo = "You can import a CSV which follows the " +
                "question, answer format with speech marks \n surrounding both and new lines indicated by backslash and n";
        String exportCSVInfo = "You will get an exported csv following the same lines as import versions";
        String exportHTMLInfo = "You can export your decks to a HTML page, and then print to PDF for portability";
        hintView.setOnMouseClicked(_-> CustomAlert.info(owner, importInfo + "\n" + "\n" + exportCSVInfo + "\n" + "\n" + exportHTMLInfo));

        Group importExportButtons = new Group(importCSV.button(), exportCSV.button(), exportHTML.button(), importIcon, exportIcon, htmlIcon);
        nav.switchContent(contentLayer, getBackView(contentLayer), importExportButtons, hintView);
    }
    private void smMouseClick() throws IOException {
        ScreenManager.switchScene(new StudyModesScreen(ac, sc, fc).create(), ac.getCurrentUser().getEmail());
    }

}
