package com.kgromov.views;

import com.kgromov.components.ContributorForm;
import com.kgromov.domain.Contributor;
import com.kgromov.service.ContributorService;
import com.vaadin.flow.component.Component;
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

import static com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.STRETCH;

@Route("/contributors")
public class ContributorView extends VerticalLayout {
    private final ContributorService contributorService;
    private final TextField filter = new TextField();
    private final Grid<Contributor> grid = new Grid<>(Contributor.class);
    private final ContributorForm form;

    public ContributorView(ContributorService contributorService, ContributorForm form) {
        this.contributorService = contributorService;
        this.form = form;

        setSizeFull();
        this.configureGrid();
        form.setChangeHandler(() -> {
            form.setVisible(false);
            applyFilter(filter.getValue());
        });
        add(this.getToolbar(), this.getContent());
        applyFilter(null);
    }

    private void configureGrid() {
        grid
                .asSingleSelect()
                .addValueChangeListener(e -> form.editContributor(e.getValue()));

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
        addNewButton.addClickListener(e -> form.editContributor(new Contributor()));

        HorizontalLayout toolbar = new HorizontalLayout(filter, addNewButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void applyFilter(String filterValue) {
        this.grid.setItems(contributorService.findContributors(filterValue));
    }
}
