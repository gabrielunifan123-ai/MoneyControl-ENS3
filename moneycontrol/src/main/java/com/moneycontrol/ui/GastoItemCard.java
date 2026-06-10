package com.moneycontrol.ui;

import com.moneycontrol.model.Categoria;
import com.moneycontrol.model.Gasto;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.Map;
import java.util.function.Consumer;

public class GastoItemCard {

    private static final Map<Categoria, String> EMOJI = Map.of(
        Categoria.ALIMENTACAO, "🍽️",
        Categoria.TRANSPORTE,  "🚗",
        Categoria.LAZER,       "🎮",
        Categoria.SAUDE,       "❤️",
        Categoria.OUTROS,      "🏷️"
    );

    private static final Map<Categoria, String> TAG_STYLE = Map.of(
        Categoria.ALIMENTACAO, "-fx-background-color:#E1F5EE; -fx-text-fill:#085041;",
        Categoria.TRANSPORTE,  "-fx-background-color:#E6F1FB; -fx-text-fill:#0C447C;",
        Categoria.LAZER,       "-fx-background-color:#FAEEDA; -fx-text-fill:#633806;",
        Categoria.SAUDE,       "-fx-background-color:#FCEBEB; -fx-text-fill:#791F1F;",
        Categoria.OUTROS,      "-fx-background-color:#F1EFE8; -fx-text-fill:#444441;"
    );

    public static HBox criar(Gasto g, boolean showDelete, Consumer<Gasto> onDelete) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 0, 12, 0));
        row.setStyle("-fx-border-color: transparent transparent #eeeeee transparent; -fx-border-width: 0 0 0.5 0;");

        // Icon
        Label iconLbl = new Label(EMOJI.getOrDefault(g.getCategoria(), "🏷️"));
        iconLbl.setStyle(
            "-fx-font-size:18px; -fx-background-color:#f5f5f0; -fx-background-radius:50%;" +
            "-fx-min-width:38px; -fx-min-height:38px; -fx-max-width:38px; -fx-max-height:38px;" +
            "-fx-alignment:center;"
        );

        // Info
        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label descLbl = new Label(g.getDescricao());
        descLbl.setStyle("-fx-font-size:14px; -fx-font-weight:500; -fx-text-fill:#222;");
        descLbl.setMaxWidth(Double.MAX_VALUE);

        HBox meta = new HBox(6);
        meta.setAlignment(Pos.CENTER_LEFT);
        Label dataLbl = new Label(g.getDataFormatada() + " ·");
        dataLbl.setStyle("-fx-font-size:12px; -fx-text-fill:#888;");

        Label catTag = new Label(g.getCategoria().getLabel());
        String tagStyle = TAG_STYLE.getOrDefault(g.getCategoria(), "-fx-background-color:#F1EFE8; -fx-text-fill:#444;");
        catTag.setStyle(tagStyle + " -fx-font-size:11px; -fx-font-weight:500; -fx-padding:2 8 2 8; -fx-background-radius:20;");

        meta.getChildren().addAll(dataLbl, catTag);
        info.getChildren().addAll(descLbl, meta);

        // Right side
        VBox right = new VBox(6);
        right.setAlignment(Pos.CENTER_RIGHT);

        Label valorLbl = new Label(g.getValorFormatado());
        valorLbl.setStyle("-fx-font-size:15px; -fx-font-weight:500; -fx-text-fill:#a32d2d;");

        right.getChildren().add(valorLbl);

        if (showDelete && onDelete != null) {
            Button btnDel = new Button("🗑");
            btnDel.setStyle(
                "-fx-background-color:#fff0f0; -fx-text-fill:#a32d2d;" +
                "-fx-border-color:#f09595; -fx-border-radius:6; -fx-background-radius:6;" +
                "-fx-border-width:0.5; -fx-padding:4 8 4 8; -fx-font-size:12px; -fx-cursor:hand;"
            );
            btnDel.setOnAction(e -> onDelete.accept(g));
            right.getChildren().add(btnDel);
        }

        row.getChildren().addAll(iconLbl, info, right);
        return row;
    }
}
