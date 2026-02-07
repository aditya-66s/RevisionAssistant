package Screens;

import Components.ContentNavigator;
import Components.CustomAlert;
import Components.ScreenManager;
import Components.UIComponents;
import Controllers.AuthenticationController;
import RecordClasses.PasswordBundle;
import RecordClasses.RectangleButtonBundle;
import RecordClasses.TextBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class SettingsScreen extends SystemScreens{
    private final Group background = createBackground("System Settings");
    private final AuthenticationController ac;

    public SettingsScreen(AuthenticationController ac) {
        this.ac = ac;
    }
    // Function returning the scene displays all options in settings
    public Scene create() {
        Pane root = new Pane();
        root.getStyleClass().add("screens");
        getTitle().setLayoutX(130);
        ImageView back = ui.createBRImageView("back");
        back.setOnMouseClicked(_ -> ScreenManager.goBack());

        Group contentLayer = new Group();
        // Creating Buttons for Settings
        RectangleButtonBundle stylesheetOptions = ui.buildRectangularButtonBundle(rectW, rectH, "Stylesheet Options", "buttonText", 310, 241);
        ImageView sOIcon = createIcon(310, 239, "/images/SettingsScreen/stylesheets.png");

        RectangleButtonBundle accessibility = ui.buildRectangularButtonBundle(rectW, rectH, "Accessibility", "buttonText", 310, 412);
        ImageView aIcon = createIcon(310, 412, "/images/SettingsScreen/accessibility.png");

        RectangleButtonBundle audioControl = ui.buildRectangularButtonBundle(rectW, rectH, "Audio Control", "buttonText", 310, 583);
        ImageView aCIcon = createIcon(310, 583, "/images/SettingsScreen/audio.png");

        RectangleButtonBundle others = ui.buildRectangularButtonBundle(rectW, rectH, "Others", "buttonText", 310, 755);
        ImageView oIcon = createIcon(310, 755, "/images/SettingsScreen/others.png");
        others.button().setOnMouseClicked(_ -> buildOthersContent(contentLayer));

        Group menuLayer = new Group(stylesheetOptions.button(), accessibility.button(), audioControl.button(), others.button(), sOIcon, aIcon, aCIcon, oIcon);
        contentLayer.getChildren().add(menuLayer);
        root.getChildren().addAll(background, back, contentLayer);
        Scene scene = new Scene(root, 1920, 1057);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());
        return scene;
    }

    // Procedure to create and display all buttons when Other Options is selected
    private void buildOthersContent(Group contentLayer) {
        RectangleButtonBundle changeEmail = ui.buildRectangularButtonBundle(rectW, rectH, "Change Email", "buttonText", 293, 315);
        ImageView emailIcon = createIcon(293, 310, "/images/SettingsScreen/whiteUser.png");
        changeEmail.button().setOnMouseClicked(_ -> buildChangeEmail(contentLayer));

        RectangleButtonBundle changeRw = ui.buildRectangularButtonBundle(rectW, rectH, "Change Word", "buttonText", 293, 485);
        ImageView recoveryIcon = createIcon(293, 480, "/images/SettingsScreen/whiteRecovery.png");
        changeRw.button().setOnMouseClicked(_ -> buildChangeRW(contentLayer));

        RectangleButtonBundle changePw = ui.buildRectangularButtonBundle(rectW, rectH, "Change Password", "buttonText", 293, 656);
        ImageView pwIcon = createIcon(293, 651, "/images/SettingsScreen/whiteLock.png");
        changePw.button().setOnMouseClicked(_ -> buildChangePassword(contentLayer));

        Group otherOptionsLayer = new Group(changeEmail.button(), changePw.button(), changeRw.button(), emailIcon, recoveryIcon, pwIcon);

        nav.switchContent(contentLayer, otherOptionsLayer, getBackView(contentLayer));
    }
    // Procedure to create and display all input fields and buttons when Change Email is selected
    private void buildChangeEmail(Group contentLayer) {
        PasswordBundle passwordInput = originalPasswordInput();
        String userIcon =  "/images/AuthScreen/user.png";
        TextBundle newEmailInput = ui.buildTextFieldInput(293, 412, rectW, 388, 422, "Enter Email", userIcon, 292,  416, 95, 90);
        newEmailInput.tf().getStyleClass().add("resetTextField");
        TextBundle confirmNewEmailInput = ui.buildTextFieldInput(293, 583, rectW, 388, 593, "Confirm Email", userIcon, 292,  587, 95, 90);
        confirmNewEmailInput.tf().getStyleClass().add("resetTextField");
        RectangleButtonBundle changeEmail = ui.buildRectangularButtonBundle(rectW, rectH, "Change Email", "buttonText", 293, 755);

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
        } else if(ac.getCurrentUser().getEmail().equals(confirmNewEmailInput.tf().getText())) {
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
        TextBundle newRecovery = ui.buildTextFieldInput(293, 412, rectW, 388, 422, "Enter Recovery Word", recoveryIcon, 292,  416, 95, 90);
        newRecovery.tf().getStyleClass().add("resetTextField");
        TextBundle confirmNewRecovery = ui.buildTextFieldInput(293, 583, rectW, 388, 593, "Confirm Recovery Word", recoveryIcon, 292,  587, 95, 90);
        confirmNewRecovery.tf().getStyleClass().add("resetTextField");
        RectangleButtonBundle changeRecoveryWord = ui.buildRectangularButtonBundle(rectW, rectH, "Change Recovery Word", "buttonText", 293, 755);

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
        PasswordBundle newPassword = ui.buildPasswordInput(293, 412, rectW, 388, 422, "Enter New Password", 293, 419, 1535, 438);
        attachStylesheetToPWField(newPassword);
        PasswordBundle cNewPassword = ui.buildPasswordInput(293, 583, rectW, 388, 593, "Enter New Password", 293, 589, 1535, 609);
        attachStylesheetToPWField(cNewPassword);
        RectangleButtonBundle changePassword = ui.buildRectangularButtonBundle(rectW, rectH, "Change Password", "buttonText", 293, 755);

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
        PasswordBundle passwordInput = ui.buildPasswordInput(293, 241, rectW, 388, 251,"Enter Password", 293, 247, 1535, 267);
        attachStylesheetToPWField(passwordInput);
        return passwordInput;
    }
    // Adding the style class to both fields of a password bundle
    private void attachStylesheetToPWField(PasswordBundle passwordInput) {
        passwordInput.password().getStyleClass().add("resetTextField");
        passwordInput.copyPw().getStyleClass().add("resetTextField");
    }
}
