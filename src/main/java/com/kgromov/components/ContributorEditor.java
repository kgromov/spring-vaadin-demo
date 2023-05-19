package com.kgromov.components;

import com.kgromov.domain.Contributor;
import com.kgromov.repository.ContributorRepository;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@UIScope
public class ContributorEditor extends VerticalLayout implements KeyNotifier {
    private final ContributorRepository repository;
    private Contributor selectedContributor;

    /* Fields to edit properties in Contributor entity */
    TextField login = new TextField("Login");
    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");
    IntegerField count = new IntegerField("Count");

    /* Action buttons */
    Button save = new Button("Save", VaadinIcon.CHECK.create());
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    Binder<Contributor> binder = new Binder<>(Contributor.class);
    private ChangeHandler changeHandler;

    @Autowired
    public ContributorEditor(ContributorRepository repository) {
        this.repository = repository;

        add(login, firstName, lastName, count, actions);

        // bind using naming convention
        binder.bindInstanceFields(this);

        // Configure and style components
        setSpacing(true);

        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        // wire action buttons to save, delete and reset
        save.addClickListener(e -> save());
        save.addClickShortcut(Key.ENTER);

        delete.addClickListener(e -> delete());
        delete.addClickShortcut(Key.DELETE);

        cancel.addClickListener(e -> editCustomer(selectedContributor));
        cancel.addClickShortcut(Key.ESCAPE);
        setVisible(false);
    }

    void delete() {
        repository.delete(selectedContributor);
        changeHandler.onChange();
    }

    void save() {
        repository.save(selectedContributor);
        changeHandler.onChange();
    }

    public final void editCustomer(Contributor contributor) {
        if (contributor == null) {
            setVisible(false);
            return;
        }
        final boolean isNew = contributor.getId() != null;
        if (isNew) {
            selectedContributor = contributor;
        }
        else {
            selectedContributor = repository.findById(contributor.getId()).orElseThrow();
        }

        // Bind customer properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        binder.setBean(selectedContributor);
        setVisible(true);
        firstName.focus();
    }

    public interface ChangeHandler {
        void onChange();
    }

    public void setChangeHandler(ChangeHandler handler) {
        // ChangeHandler is notified when either save or delete
        // is clicked
        changeHandler = handler;
    }
}
