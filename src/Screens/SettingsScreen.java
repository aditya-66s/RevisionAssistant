package Screens;

import Components.CustomAlert;
import Components.ScreenManager;
import Components.UIComponents;
import Controllers.AuthenticationController;
import Controllers.SettingsController;
import Models.Settings;
import RecordClasses.PasswordBundle;
import RecordClasses.RectangleButtonBundle;
import RecordClasses.TextBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class SettingsScreen extends SystemScreens{
    // Constants
    private static final int titleX = 290;
    private static final int y1 = 241;
    private static final int oneTimeOffset = -2;
    private static final int y2 = 412;
    private static final int y3 = 583;
    private static final int y4 = 755;
    private static final int saveY = 755;
    private static final int iTX = 180;
    private static final int iTY = 270;
    private static final int ssBtnY = 300;
    private static final int othersY1 = 315;
    private static final int othersY2 = 485;
    private static final int othersY3 = 656;
    private static final int one = 1;
    private static final int four = 4;
    private static final int ten = 10;
    private static final int fieldX = 388;
    private static final int fieldH = 98;
    private static final int passwordIconY1 = 247;
    private static final int passwordIconY2 = 419;
    private static final int passwordIconY3 = 589;
    private static final int passwordRIconX = 1535;
    private static final int passwordRIconY1 = 267;
    private static final int passwordRIconY2 = 438;
    private static final int passwordRIconY3 = 609;
    private static final int fieldY1 = 241;
    // Common
    private static final int labelX = 150;
    private static final int valueX = 1500;
    private static final int inputX = 1600;
    private static final int sliderX = 250;
    private static final int toggleX = 1200;
    // SFX
    private static final int sfxY = 270;
    private static final int sfxInputY = 290;
    private static final int sfxSliderY = 300;
    // TTS
    private static final int ttsY = 470;
    private static final int ttsInputY = 490;
    private static final int ttsSliderY = 500;
    // Toggles
    private static final int toggleMuteY = 630;
    private static final int toggleMuteBoxY = 560;
    private static final int toggleTTSY = 730;
    private static final int toggleTTSBoxY = 660;
    // Darkness
    private static final int darknessY = 300;
    private static final int darknessInputY = 490;
    private static final int darknessSliderY = 490;

    private final Group background = createBackground("System Settings");
    private final AuthenticationController ac;
    private final SettingsController sC;
    private final Settings currentSettings;
    private Settings draftSettings;
    private boolean saved = false;
    public SettingsScreen(AuthenticationController ac, SettingsController sC){
        this.ac = ac;
        this.sC = sC;
        // Load settings if not already loaded
        if (sC.getCurrentSettings() == null) {
            try {
                Settings userSettings = sC.loadSettings(ac.getEmail());
                sC.setCurrentSettings(userSettings);
            } catch (IOException e) {
                // Default Setting set as a fallback
                Settings userSettings = new Settings();
                sC.setCurrentSettings(userSettings);
            }
        }
        this.currentSettings = sC.getCurrentSettings();
        // Use existing draft if available, otherwise create new one
        if (sC.getDraftSettings() == null) {
            this.draftSettings = currentSettings.copy();
            sC.setDraftSettings(this.draftSettings);
        } else {
            this.draftSettings = sC.getDraftSettings();
        }
    }

    // Function returning the scene displays all options in settings
    public Scene create() {
        Pane root = new Pane();
        root.getStyleClass().add("screens");
        Scene scene = new Scene(root, sceneW, sceneH);
        getTitle().setLayoutX(titleX);
        ImageView back = getSwitchScreenBackView(ac);

        Group contentLayer = new Group();
        // Creating Buttons for Settings
        RectangleButtonBundle stylesheetOptions = ui.buildRectangularButtonBundle(rectW, rectH, "Stylesheet Options", "buttonText", fourX, y1);
        ImageView sOIcon = createIcon(fourX, y1+oneTimeOffset, "/images/SettingsScreen/stylesheets.png");
        stylesheetOptions.button().setOnMouseClicked(_-> buildStylesheetOptionsContent(contentLayer, scene));

        RectangleButtonBundle accessibility = ui.buildRectangularButtonBundle(rectW, rectH, "Accessibility", "buttonText", fourX, y2);
        ImageView aIcon = createIcon(fourX, y2, "/images/SettingsScreen/accessibility.png");
        accessibility.button().setOnMouseClicked(_-> {
            try {
                buildAccessibilityOptionsContent(contentLayer, scene);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        RectangleButtonBundle audioControl = ui.buildRectangularButtonBundle(rectW, rectH, "Audio Control", "buttonText", fourX, y3);
        ImageView aCIcon = createIcon(fourX, y3, "/images/SettingsScreen/audio.png");
        audioControl.button().setOnMouseClicked(_-> buildAudioControlContent(contentLayer, scene));

        RectangleButtonBundle others = ui.buildRectangularButtonBundle(rectW, rectH, "Others", "buttonText", fourX, y4);
        ImageView oIcon = createIcon(fourX, y4, "/images/SettingsScreen/others.png");
        others.button().setOnMouseClicked(_ -> buildOthersContent(contentLayer));

        Group menuLayer = new Group(stylesheetOptions.button(), accessibility.button(), audioControl.button(), others.button(), sOIcon, aIcon, aCIcon, oIcon);
        contentLayer.getChildren().add(menuLayer);
        root.getChildren().addAll(background, back, contentLayer);
        return scene;
    }

    // Re-usable save button
    private RectangleButtonBundle saveRect(Scene scene){
        RectangleButtonBundle save = ui.buildRectangularButtonBundle(rectW, rectH, "Save Settings", "buttonText", fourX, saveY);
        save.button().setOnMouseClicked(_ -> {
            try {
                sC.saveSettings(draftSettings);
                sC.setCurrentSettings(draftSettings);
                sC.setDraftSettings(draftSettings.copy());
                CustomAlert.success(scene.getWindow(), "Success, changes saved.");
                saved = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return save;
    }

    // Procedure creating the UI for user to pick between stylesheet options
    private void buildStylesheetOptionsContent(Group contentLayer, Scene scene) {
        saved = false;

        Text instructionText = new Text(iTX, iTY, "Pick your stylesheet option:");
        instructionText.getStyleClass().add("instructionText");

        MenuItem darkMode = new MenuItem("Dark Mode");
        MenuItem lightMode = new MenuItem("Light Mode");
        MenuItem highContrast = new MenuItem("High Contrast");

        darkMode.getStyleClass().add("options");
        lightMode.getStyleClass().add("options");
        highContrast.getStyleClass().add("options");

        RectangleButtonBundle styles = ui.buildRectangularButtonBundle(rectW, rectH, "Stylesheet Options", "buttonText", fourX, ssBtnY);
        ContextMenu menu = new ContextMenu(darkMode, lightMode, highContrast);
        menu.getStyleClass().add("options-menu");
        styles.button().setOnMouseClicked(_-> menu.show(styles.button(), Side.BOTTOM, zero, zero));

        String infoMessage = "This is only a preview. \n The change will persist until you exist settings. \n Press save to ensure changes persist";
        darkMode.setOnAction(_ -> {
            draftSettings.setTheme("dark");
            styles.buttonText().setText("Dark Mode");
            previewTheme(scene, draftSettings.getTheme());
            createInfoAlert(scene, infoMessage);
        });
        lightMode.setOnAction(_ -> {
            draftSettings.setTheme("light");
            styles.buttonText().setText("Light Mode");
            previewTheme(scene, draftSettings.getTheme());
            createInfoAlert(scene, infoMessage);
        });
        highContrast.setOnAction(_ -> {
            draftSettings.setTheme("highContrast");
            styles.buttonText().setText("High Contrast Mode");
            previewTheme(scene, draftSettings.getTheme());
            createInfoAlert(scene, infoMessage);
        });

        RectangleButtonBundle save = saveRect(scene);
        ImageView backView = getBackView(contentLayer);
        backView.setOnMouseClicked(_ -> {
            boolean isConfirmed = false;
            if(!saved) isConfirmed = CustomAlert.confirm(scene.getWindow(), "Confirmation of Save", "Changes only persist if you click save.");
            if(isConfirmed || saved){
                nav.goBack(contentLayer);
            }
        });
        nav.switchContent(contentLayer, instructionText, styles.button(), save.button(), backView);
    }
    // ERROR: I APPLIED IT TO THE FRICKING PANE INSTEAD OF THE SCENE, TOOK ME 2 HOURS TO REALISE
    // Theme selected with be applied to their screen to give the user a preview
    private void previewTheme(Scene root, String theme){
        // Removing any style sheet applied currently to avoid theme conflict/stacking
        root.getStylesheets().clear();
        // styles.css contains shared layout, fonts and component styling
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());
        String themePath;
        switch (theme){
            case "light" -> themePath = "/styles/light.css";
            case "highContrast" -> themePath = "/styles/highContrast.css";
            default -> themePath = "/styles/dark.css";
        }
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource(themePath)).toExternalForm());
    }
    // Displaying information alert windows to user
    private void createInfoAlert(Scene scene, String message){
        Window alertWindow = scene.getWindow();
        CustomAlert.info(alertWindow, message);
    }

    // Procedure to build accessibility settings content
    private void buildAccessibilityOptionsContent(Group contentLayer, Scene scene) throws IOException {
        saved = false;
        Rectangle brightnessOverlay = getBrightnessOverlay(scene);
        System.out.println("Loading darkness value: " + draftSettings.getDarkness()); // DEBUG
        brightnessOverlay.setOpacity(draftSettings.getDarkness());
        Text darknessText = new Text("Darkness Overlay:");
        darknessText.getStyleClass().add("instructionText");
        darknessText.setLayoutX(labelX);
        darknessText.setLayoutY(darknessY);

        Text darknessValue = new Text(String.format("%.0f%%", draftSettings.getDarkness() * 100));
        darknessValue.getStyleClass().add("instructionText");
        darknessValue.setLayoutX(valueX);
        darknessValue.setLayoutY(darknessY);

        TextField darknessInput = new TextField();
        darknessInput.getStyleClass().add("sliderInputAlt");
        darknessInput.setLayoutX(inputX);
        darknessInput.setLayoutY(darknessInputY);

        darknessInput.setText(String.format("%.0f%%", draftSettings.getDarkness() * 100));

        Slider darknessSlider = new Slider(0, 0.8, draftSettings.getDarkness());
        // Darkness/Brightness Control
        darknessSlider.setLayoutX(sliderX);
        darknessSlider.setLayoutY(darknessSliderY);
        darknessSlider.setMajorTickUnit(0.1);
        darknessSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double darkness = newVal.doubleValue();
            draftSettings.setDarkness(darkness);
            brightnessOverlay.setOpacity(darkness);
            System.out.println("Set darkness to: " + darkness); // DEBUG
            darknessValue.setText(String.format("%.0f%%", darkness * 100));
            darknessInput.setText(String.format("%.0f%%", darkness * 100));
        });

        String infoMessage = "Changes to brightness will only persist after save button is pressed";

        // Can be changed via user input too
        darknessInput.setOnAction(_ -> {
            try{
                double inputValue = Double.parseDouble(darknessInput.getText());
                // Clamp between 0-80
                inputValue = Math.max(0, Math.min(80, inputValue));
                double darkness = inputValue / 100.0;
                draftSettings.setDarkness(darkness);
                brightnessOverlay.setOpacity(darkness);
                darknessSlider.setValue(darkness);
                darknessValue.setText(String.format("%.0f%%", darkness * 100));
                darknessInput.setText(String.format("%.0f", inputValue));
            } catch (NumberFormatException e) {
                // Reset to current value if invalid input
                darknessInput.setText(String.format("%.0f", draftSettings.getDarkness() * 100));
            }
        });
        RectangleButtonBundle save = saveRect(scene);
        ImageView backView = getBackView(contentLayer);
        backView.setOnMouseClicked(_ -> {
            boolean isConfirmed = false;
            if(!saved) isConfirmed = CustomAlert.confirm(scene.getWindow(), "Confirmation of Save", infoMessage);
            if(isConfirmed || saved){
                nav.goBack(contentLayer);
            }
        });
        nav.switchContent(contentLayer, darknessText, darknessValue, darknessSlider, darknessInput, save.button(), backView);
    }
    // Function to get the scene's original brightnessOverlay
    private Rectangle getBrightnessOverlay(Scene scene) throws IOException {
        Rectangle brightnessOverlay = null;
        if(scene.getRoot() instanceof StackPane wrapper){
            // Check every node in the StackPane to get the brightness overlay and set it to the new one
            for(var node : wrapper.getChildren()){
                if(node instanceof Rectangle rect && rect.getWidth() == sceneW && rect.getHeight() == sceneH){
                    brightnessOverlay = rect;
                    break;
                }
            }
        }
        if(brightnessOverlay == null){
            brightnessOverlay = ScreenManager.getBrightnessOverlay(ac.getEmail());
        }
        return brightnessOverlay;
    }

    // Procedure to build audio settings content, with sliders and check boxes for
    private void buildAudioControlContent(Group contentLayer, Scene scene){
        saved = false;
        Text sfxText = new Text("Sound Effects Volume: ");
        sfxText.getStyleClass().add("instructionText");
        sfxText.setLayoutX(labelX);
        sfxText.setLayoutY(sfxY);

        Text sfxValue = new Text(String.format("%.0f%%", draftSettings.getSFXVolume() * 100));
        sfxValue.getStyleClass().add("instructionText");
        sfxValue.setLayoutX(valueX);
        sfxValue.setLayoutY(sfxY);

        TextField sfxInput = new TextField(String.format("%.0f%%", draftSettings.getSFXVolume() * 100));
        sfxInput.getStyleClass().add("sliderInputAlt");
        sfxInput.setLayoutX(inputX);
        sfxInput.setLayoutY(sfxInputY);

        Slider sfxSlider = new Slider(0, 1.0, draftSettings.getSFXVolume());
        sfxSlider.setLayoutX(sliderX);
        sfxSlider.setLayoutY(sfxSliderY);
        sfxSlider.setMajorTickUnit(0.1);

        Text  ttsText = new Text("Text-To-Speech Volume: ");
        ttsText.getStyleClass().add("instructionText");
        ttsText.setLayoutX(labelX);
        ttsText.setLayoutY(ttsY);

        Text ttsValue = new Text(String.format("%.0f%%", draftSettings.getTTSVolume() * 100));
        ttsValue.getStyleClass().add("instructionText");
        ttsValue.setLayoutX(valueX);
        ttsValue.setLayoutY(ttsY);

        TextField ttsInput = new TextField(String.format("%.0f%%", draftSettings.getTTSVolume() * 100));
        ttsInput.getStyleClass().add("sliderInputAlt");
        ttsInput.setLayoutX(inputX);
        ttsInput.setLayoutY(ttsInputY);

        Slider ttsSlider = new Slider(0, 1.0, draftSettings.getTTSVolume());
        ttsSlider.setLayoutX(sliderX);
        ttsSlider.setLayoutY(ttsSliderY);
        ttsSlider.setMajorTickUnit(0.1);

        Text toggleMuteText = new Text("Toggle Mute: ");
        toggleMuteText.getStyleClass().add("instructionText");
        toggleMuteText.setLayoutX(labelX);
        toggleMuteText.setLayoutY(toggleMuteY);

        CheckBox toggleMute = new CheckBox();
        toggleMute.setSelected(draftSettings.isMuted());
        toggleMute.getStyleClass().add("toggles");
        toggleMute.setLayoutX(toggleX);
        toggleMute.setLayoutY(toggleMuteBoxY);
        toggleMute.setOnAction(_ -> {
            draftSettings.setMuted(toggleMute.isSelected());
            onToggle(toggleMute, sfxInput, sfxValue, sfxSlider, false);
        });

        Text toggleTTSText = new Text("Toggle TTS: ");
        toggleTTSText.getStyleClass().add("instructionText");
        toggleTTSText.setLayoutX(labelX);
        toggleTTSText.setLayoutY(toggleTTSY);

        CheckBox toggleTTS = new CheckBox();
        toggleTTS.setSelected(draftSettings.isTTSEnabled());
        toggleTTS.getStyleClass().add("toggles");
        toggleTTS.setLayoutX(toggleX);
        toggleTTS.setLayoutY(toggleTTSBoxY);
        toggleTTS.setOnAction(_ -> {
            draftSettings.setTTSEnabled(toggleTTS.isSelected());
            onToggle(toggleTTS, ttsInput, ttsValue, ttsSlider, true);
        });

        ttsSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double volume = newVal.doubleValue();
            enforceConsistency(volume, toggleTTS, draftSettings, true);
            draftSettings.setTTSVolume(volume);
            ttsInput.setText(String.format("%.0f%%", draftSettings.getTTSVolume() * 100));
            ttsValue.setText(String.format("%.0f%%", draftSettings.getTTSVolume() * 100));
            ttsSlider.setOnMouseReleased(_-> playTestSound(volume));
        });

        ttsInput.setOnAction(_ -> {
            try{
                double inputValue = Double.parseDouble(ttsInput.getText());
                // Clamp between 0-100
                inputValue = Math.max(0, Math.min(100 , inputValue));
                double ttsVol = inputValue / 100.0;
                enforceConsistency(ttsVol, toggleTTS, draftSettings, true);
                draftSettings.setTTSVolume(ttsVol);
                playTestSound(ttsVol);
                ttsSlider.setValue(ttsVol);
                ttsValue.setText(String.format("%.0f%%", ttsVol * 100));
                ttsInput.setText(String.format("%.0f", inputValue));
            } catch (NumberFormatException e) {
                // Reset to current value if invalid input
                sfxInput.setText(String.format("%.0f", draftSettings.getTTSVolume() * 100));
            }
        });

        sfxSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double volume = newVal.doubleValue();
            enforceConsistency(volume, toggleMute, draftSettings, false);
            draftSettings.setSFXVolume(volume);
            sfxInput.setText(String.format("%.0f%%", draftSettings.getSFXVolume() * 100));
            sfxValue.setText(String.format("%.0f%%", draftSettings.getSFXVolume() * 100));
            sfxSlider.setOnMouseReleased(_-> playTestSound(volume));
        });

        sfxInput.setOnAction(_ -> {
            try{
                double inputValue = Double.parseDouble(sfxInput.getText());
                // Clamp between 0-100
                inputValue = Math.max(0, Math.min(100 , inputValue));
                double sfxVol = inputValue / 100.0;
                enforceConsistency(sfxVol, toggleMute, draftSettings, false);
                draftSettings.setSFXVolume(sfxVol);
                playTestSound(sfxVol);
                sfxSlider.setValue(sfxVol);
                sfxValue.setText(String.format("%.0f%%", sfxVol * 100));
                sfxInput.setText(String.format("%.0f", inputValue));
            } catch (NumberFormatException e) {
                // Reset to current value if invalid input
                sfxInput.setText(String.format("%.0f", draftSettings.getSFXVolume() * 100));
            }
        });

        RectangleButtonBundle save = saveRect(scene);
        ImageView backView = getBackView(contentLayer);
        backView.setOnMouseClicked(_ -> {
            boolean isConfirmed = false;
            if(!saved) isConfirmed = CustomAlert.confirm(scene.getWindow(), "Confirmation of Save", "Changes will only be saved when the save button is clicked");
            if(isConfirmed || saved){
                nav.goBack(contentLayer);
            }
        });
        Group texts = new Group(sfxText, sfxValue, sfxInput, ttsText, ttsValue, ttsInput, toggleMuteText, toggleTTSText);
        nav.switchContent(contentLayer, sfxSlider, ttsSlider, toggleMute, toggleTTS, texts, save.button(), backView);
    }
    // Update all checkboxes and sliders on specific change
    private void enforceConsistency(double volume, CheckBox checkBox, Settings draftSettings, boolean isTTS){
        if(isTTS){
            if(volume != 0){
                draftSettings.setTTSEnabled(true);
                checkBox.setSelected(draftSettings.isTTSEnabled());
            }else{
                draftSettings.setTTSEnabled(false);
                checkBox.setSelected(draftSettings.isTTSEnabled());
            }
        }else{
            if(volume != 0){
                draftSettings.setMuted(false);
                checkBox.setSelected(draftSettings.isMuted());
            }else{
                draftSettings.setMuted(true);
                checkBox.setSelected(draftSettings.isMuted());
            }
        }
    }
    // Procedure which changes screen info on check boxes
    private void onToggle(CheckBox checkBox, TextField inputField, Text value, Slider slider, boolean isTTS) {
        double volume;
        if (checkBox.isSelected()) {
            if (isTTS) {
                // get and set the default value for tts volume (80%)
                volume = new Settings().getTTSVolume();
                draftSettings.setTTSVolume(volume);
            } else {
                // Muted value
                volume = 0.0;
                draftSettings.setSFXVolume(volume);
            }
            // Change info on screen
            inputField.setText(String.format("%.0f%%", volume * 100));
            value.setText(String.format("%.0f%%", volume * 100));
            slider.setValue(volume);
        } else {
            if (isTTS) {
                volume = 0.0;
                draftSettings.setTTSVolume(volume);
            } else {
                // get and set default value for sfx (60%)
                volume = new Settings().getSFXVolume();
                draftSettings.setSFXVolume(volume);
            }
            // Change info on screen
            inputField.setText("0%");
            value.setText("0%");
            slider.setValue(volume);
        }
        // Play test sound
        playTestSound(volume);
    }
    // procedure which will play the test sound when volume is altered
    private void playTestSound(double volume) {
        try {
            // Create a temp file on the system to store the sound
            // JavaFX media requires a URI
            InputStream is = getClass().getResourceAsStream("/sounds/testSound.wav");
            File tempFile = File.createTempFile("testSound", ".wav");
            // Ensure it is deleted on exit
            tempFile.deleteOnExit();
            // Copy sound data from sound file to file
            Files.copy(Objects.requireNonNull(is), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            Media testSound = new Media(tempFile.toURI().toString());
            MediaPlayer player = new MediaPlayer(testSound);
            player.setVolume(volume);
            player.play();
        } catch (Exception _){}
    }

    // Procedure to create and display all buttons when Other Options is selected
    private void buildOthersContent(Group contentLayer) {
        RectangleButtonBundle changeEmail = ui.buildRectangularButtonBundle(rectW, rectH, "Change Email", "buttonText", threeX, othersY1);
        ImageView emailIcon = createIcon(threeX, othersY1+iconOffset, "/images/SettingsScreen/whiteUser.png");
        changeEmail.button().setOnMouseClicked(_ -> buildChangeEmail(contentLayer));

        RectangleButtonBundle changeRw = ui.buildRectangularButtonBundle(rectW, rectH, "Change Word", "buttonText", threeX, othersY2);
        ImageView recoveryIcon = createIcon(threeX, othersY2+iconOffset, "/images/SettingsScreen/whiteRecovery.png");
        changeRw.button().setOnMouseClicked(_ -> buildChangeRW(contentLayer));

        RectangleButtonBundle changePw = ui.buildRectangularButtonBundle(rectW, rectH, "Change Password", "buttonText", threeX, othersY3);
        ImageView pwIcon = createIcon(threeX, othersY3+iconOffset, "/images/SettingsScreen/whiteLock.png");
        changePw.button().setOnMouseClicked(_ -> buildChangePassword(contentLayer));

        Group otherOptionsLayer = new Group(changeEmail.button(), changePw.button(), changeRw.button(), emailIcon, recoveryIcon, pwIcon);

        nav.switchContent(contentLayer, otherOptionsLayer, getBackView(contentLayer));
    }

    // Procedure to create and display all input fields and buttons when Change Email is selected
    private void buildChangeEmail(Group contentLayer) {
        PasswordBundle passwordInput = originalPasswordInput();
        String userIcon =  "/images/AuthScreen/user.png";
        TextBundle newEmailInput = ui.buildTextFieldInput(threeX, y2, rectW, fieldH , fieldX, y2+ten, "Enter Email", userIcon, threeX-one,  y2+four, iconW, iconH);
        newEmailInput.tf().getStyleClass().add("resetTextField");
        TextBundle confirmNewEmailInput = ui.buildTextFieldInput(threeX, y3, rectW, fieldH, fieldX, y3+ten, "Confirm Email", userIcon, threeX-one,  y3+four, iconW, iconH);
        confirmNewEmailInput.tf().getStyleClass().add("resetTextField");
        RectangleButtonBundle changeEmail = ui.buildRectangularButtonBundle(rectW, rectH, "Change Email", "buttonText", threeX, y4);

        UIComponents.attachValidator(newEmailInput.tf(), UIComponents::isValidEmail, "error");
        UIComponents.attachValidator(confirmNewEmailInput.tf(), UIComponents::isValidEmail, "error");

        EventHandler<ActionEvent> changeEmailHandler =
                event -> {
                    try {
                        changeEmailSubmit(event, passwordInput, newEmailInput, confirmNewEmailInput, contentLayer);
                    } catch (NoSuchAlgorithmException | IOException e) {
                        throw new RuntimeException(e);
                    }
                };
        // You can press enter to submit, convenience
        ui.bindEnter(changeEmailHandler, newEmailInput.tf(), confirmNewEmailInput.tf());

        changeEmail.button().setOnMouseClicked(e ->
                {
                    try {
                        changeEmailSubmit(e, passwordInput, newEmailInput, confirmNewEmailInput, contentLayer);
                    } catch (NoSuchAlgorithmException | IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
        );
        Group changeEmailLayer = new Group(newEmailInput.group(),  confirmNewEmailInput.group(), changeEmail.button(), newEmailInput.icon(), confirmNewEmailInput.icon(), passwordInput.passwordGroup(), passwordInput.getIconGroup());

        nav.switchContent(contentLayer, changeEmailLayer, getBackView(contentLayer));
    }
    // Procedure which validates the inputs when the email change is submitted and will display an appropriate alert.
    private void changeEmailSubmit(Event event, PasswordBundle passwordInput, TextBundle newEmailInput, TextBundle confirmNewEmailInput, Group contentLayer) throws NoSuchAlgorithmException, IOException {
        Window alertWindow = ((Node) event.getSource()).getScene().getWindow();
        boolean validInput = UIComponents.isValidEmail(newEmailInput.tf().getText()) &&  UIComponents.isValidEmail(confirmNewEmailInput.tf().getText());
        if (!validInput) {
            String emailReq = "Email should follow the standard format";
            CustomAlert.error(alertWindow, "Invalid inputs \n" + emailReq);
        } else if(ac.getEmail().equals(confirmNewEmailInput.tf().getText())) {
            CustomAlert.error(alertWindow, "This is already the email associated with the account");
        } else if (ac.doesEmailExist(newEmailInput.tf().getText())) {
            CustomAlert.error(alertWindow, "Email already exists, try a new one");
        } else if(!ac.verifyPassword(passwordInput.password().getText(), ac.getCurrentUser().getHashedPassword(), ac.getCurrentUser().getSaltBase64())){
            CustomAlert.error(alertWindow, "Incorrect Password ");
        }else if(!Objects.equals(newEmailInput.tf().getText(), confirmNewEmailInput.tf().getText())) {
            CustomAlert.error(alertWindow, "Inputs do not match");
        }else{
            boolean updated = ac.changeEmail(ac.getCurrentUser(), newEmailInput.tf().getText());
            if (updated) {
                CustomAlert.success(alertWindow, "Success, you will be returned to the changing options");
                nav.goBack(contentLayer);
            }else{
                CustomAlert.error(alertWindow, "Problems with file, try again");
            }
        }
    }
    // Procedure to create and display all input fields and buttons when Change Recovery Word is selected
    private void buildChangeRW(Group contentLayer) {
        PasswordBundle passwordInput = originalPasswordInput();
        String recoveryIcon = "/images/AuthScreen/recover.png";
        TextBundle newRecovery = ui.buildTextFieldInput(threeX, y2, rectW, fieldH, fieldX, y2+ten, "Enter Recovery Word", recoveryIcon, threeX-one,  y2+four, iconW, iconH);
        newRecovery.tf().getStyleClass().add("resetTextField");
        TextBundle confirmNewRecovery = ui.buildTextFieldInput(threeX, y3, rectW, fieldH, fieldX, y3+ten, "Confirm Recovery Word", recoveryIcon, threeX-one,  y3+four, iconW, iconH);
        confirmNewRecovery.tf().getStyleClass().add("resetTextField");
        RectangleButtonBundle changeRecoveryWord = ui.buildRectangularButtonBundle(rectW, rectH, "Change Recovery Word", "buttonText", threeX, y4);

        UIComponents.attachValidator(newRecovery.tf(), UIComponents::isValidRecoveryW, "error");
        UIComponents.attachValidator(confirmNewRecovery.tf(), UIComponents::isValidRecoveryW, "error");

        EventHandler<ActionEvent> changeRWHandler =
                event -> {
                    try {
                        changeRWSubmit(event, passwordInput, newRecovery, confirmNewRecovery, contentLayer);
                    } catch (NoSuchAlgorithmException | IOException e) {
                        throw new RuntimeException(e);
                    }
                };
        ui.bindEnter(changeRWHandler, newRecovery.tf(), confirmNewRecovery.tf());

        changeRecoveryWord.button().setOnMouseClicked(e ->
                {
                    try {
                        changeRWSubmit(e, passwordInput, newRecovery, confirmNewRecovery, contentLayer);
                    } catch (NoSuchAlgorithmException | IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
        );
        Group changeRWLayer = new Group(newRecovery.group(),  confirmNewRecovery.group(), changeRecoveryWord.button(), newRecovery.icon(), confirmNewRecovery.icon(), passwordInput.passwordGroup(), passwordInput.getIconGroup());

        nav.switchContent(contentLayer, changeRWLayer, getBackView(contentLayer));
    }
    // Procedure which validates the inputs when the recovery word change is submitted and will display an appropriate alert.
    private void changeRWSubmit(Event event, PasswordBundle passwordInput, TextBundle newRW, TextBundle cNewRW, Group contentLayer) throws NoSuchAlgorithmException, IOException {
        Window alertWindow = ((Node) event.getSource()).getScene().getWindow();
        boolean validInput = UIComponents.isValidRecoveryW(newRW.tf().getText()) &&  UIComponents.isValidRecoveryW(cNewRW.tf().getText());
        if (!validInput) {
            String rwReq = "Recovery Word must be a minimum of 3 characters";
            CustomAlert.error(alertWindow, "Invalid inputs \n" + rwReq);
        } else if(ac.getCurrentUser().getRecoveryWord().equals(cNewRW.tf().getText())) {
            CustomAlert.error(alertWindow, "This is already the Recovery Word associated with the account");
        } else if(!ac.verifyPassword(passwordInput.password().getText(), ac.getCurrentUser().getHashedPassword(), ac.getCurrentUser().getSaltBase64())){
            CustomAlert.error(alertWindow, "Incorrect Password ");
        }else if(!Objects.equals(newRW.tf().getText(), cNewRW.tf().getText())) {
            CustomAlert.error(alertWindow, "Inputs do not match");
        }else{
            boolean updated = ac.changeRecoveryWord(ac.getCurrentUser(), newRW.tf().getText());
            if (updated) {
                CustomAlert.success(alertWindow, "Success, you will be returned to the changing options");
                nav.goBack(contentLayer);
            }else{
                CustomAlert.error(alertWindow, "Problems with file, try again");
            }
        }
    }

    // Procedure to create and display all input fields and buttons when Change Password is selected
    private void buildChangePassword(Group contentLayer) {
        PasswordBundle passwordInput = originalPasswordInput();
        PasswordBundle newPassword = ui.buildPasswordInput(threeX, y2, rectW, fieldX, y2+ten, "Enter New Password", threeX, passwordIconY2, passwordRIconX, passwordRIconY2);
        attachStylesheetToPWField(newPassword);
        PasswordBundle cNewPassword = ui.buildPasswordInput(threeX, y3, rectW, fieldX, y3+ten, "Enter New Password", threeX, passwordIconY3, passwordRIconX, passwordRIconY3);
        attachStylesheetToPWField(cNewPassword);
        RectangleButtonBundle changePassword = ui.buildRectangularButtonBundle(rectW, rectH, "Change Password", "buttonText", threeX, y4);

        UIComponents.attachValidator(newPassword.password(), UIComponents::isValidPassword, "error", newPassword.copyPw());
        UIComponents.attachValidator(cNewPassword.password(), UIComponents::isValidPassword, "error", cNewPassword.copyPw());

        EventHandler<ActionEvent> changePasswordHandler =
                event -> {
                    try {
                        changePasswordSubmit(event, passwordInput, newPassword, cNewPassword, contentLayer);
                    } catch (NoSuchAlgorithmException | IOException e) {
                        throw new RuntimeException(e);
                    }
                };
        ui.bindEnter(changePasswordHandler, passwordInput.password(), passwordInput.copyPw(), cNewPassword.password(), cNewPassword.copyPw(), contentLayer);

        changePassword.button().setOnMouseClicked(e ->
                {
                    try {
                        changePasswordSubmit(e, passwordInput, newPassword, cNewPassword, contentLayer);
                    } catch (NoSuchAlgorithmException | IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
        );
        Group changeEmailLayer = new Group(newPassword.passwordGroup(), newPassword.getIconGroup(), cNewPassword.passwordGroup(), cNewPassword.getIconGroup(), changePassword.button(), passwordInput.passwordGroup(), passwordInput.getIconGroup());

        nav.switchContent(contentLayer, changeEmailLayer, getBackView(contentLayer));
    }
    // Procedure which validates the inputs when the password change is submitted and will display an appropriate alert.
    private void changePasswordSubmit(Event event, PasswordBundle oldPassword, PasswordBundle newPassword, PasswordBundle confirmNewPassword, Group contentLayer) throws NoSuchAlgorithmException, IOException {
        Window alertWindow = ((Node) event.getSource()).getScene().getWindow();
        boolean validInput = UIComponents.isValidPassword(newPassword.password().getText()) &&  UIComponents.isValidPassword(confirmNewPassword.password().getText());
        if (!validInput) {
            String passwordReq = "Password should have a minimum of 6 characters, including a special character";
            CustomAlert.error(alertWindow, "Invalid inputs \n" + passwordReq);
        } else if(!ac.verifyPassword(oldPassword.password().getText(), ac.getCurrentUser().getHashedPassword(), ac.getCurrentUser().getSaltBase64())){
            CustomAlert.error(alertWindow, "Incorrect Password ");
        }else if(!Objects.equals(confirmNewPassword.password().getText(), newPassword.password().getText())) {
            CustomAlert.error(alertWindow, "Inputs do not match");
        }else if(ac.verifyPassword(newPassword.password().getText(), ac.getCurrentUser().getHashedPassword(), ac.getCurrentUser().getSaltBase64())) {
            CustomAlert.error(alertWindow, "Same password as before");
        }else{
            boolean updated = ac.resetPassword(ac.getCurrentUser(), newPassword.password().getText());
            if (updated) {
                CustomAlert.success(alertWindow, "Success, you will be returned to the changing options");
                nav.goBack(contentLayer);
            }else{
                CustomAlert.error(alertWindow, "Problems with file, try again");
            }
        }
    }
    // Consistent field throughout all changing data options, extracted into a field
    private PasswordBundle originalPasswordInput(){
        PasswordBundle passwordInput = ui.buildPasswordInput(threeX, fieldY1, rectW, fieldX, fieldY1+ten,"Enter Password", threeX, passwordIconY1, passwordRIconX, passwordRIconY1);
        attachStylesheetToPWField(passwordInput);
        return passwordInput;
    }
    // Adding the style class to both fields of a password bundle
    private void attachStylesheetToPWField(PasswordBundle passwordInput) {
        passwordInput.password().getStyleClass().add("resetTextField");
        passwordInput.copyPw().getStyleClass().add("resetTextField");
    }
}
