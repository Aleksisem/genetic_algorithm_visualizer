module com.lex.genetics {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.lex.genetics to javafx.fxml;
    opens com.lex.genetics.controller to javafx.fxml; 
    
    exports com.lex.genetics;
}
