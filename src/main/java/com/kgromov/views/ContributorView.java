package com.kgromov.views;

import com.kgromov.components.ContributorEditor;
import com.kgromov.domain.Contributor;
import com.kgromov.repository.ContributorRepository;
import com.kgromov.service.ContributorService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Route("/contributors")
public class ContributorView extends VerticalLayout {
    private final ContributorService contributorService;
    private final ContributorEditor editor;
    private final Grid<Contributor> grid = new Grid<>(Contributor.class);
    private final TextField filter = new TextField();

    public ContributorView(ContributorService contributorService, ContributorEditor editor) {
        this.contributorService = contributorService;
        this.editor = editor;

        setSizeFull();
        this.configureGrid();
        add(getToolbar(), grid, editor);

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            applyFilter(filter.getValue());
        });

        applyFilter(null);
    }

    private void configureGrid() {
        grid
                .asSingleSelect()
                .addValueChangeListener(e -> editor.editCustomer(e.getValue()));

        String[] columns = Arrays.stream(Contributor.class.getDeclaredFields()).map(Field::getName).toArray(String[]::new);
        grid.setColumns(columns);
        grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);
        grid.setSizeFull();
        grid.addClassName("list-view");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private HorizontalLayout getToolbar() {
        filter.setPlaceholder("Search for contributor ...");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(field -> applyFilter(field.getValue()));

        Button addNewButton = new Button("New contributor", VaadinIcon.PLUS.create());
        Random random = new Random();
        Supplier<Contributor> defaultContributor = () -> Contributor.builder().id(random.nextLong()).login("").firstName("").lastName("").build();
        addNewButton.addClickListener(e -> editor.editCustomer(defaultContributor.get()));

        HorizontalLayout toolbar = new HorizontalLayout(filter, addNewButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void applyFilter(String filterValue) {
        this.grid.setItems(contributorService.findContributors(filterValue));
    }
}
