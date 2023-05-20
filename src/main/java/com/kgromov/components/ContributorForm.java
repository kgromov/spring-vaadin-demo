package com.kgromov.components;

import com.kgromov.domain.Contributor;
import com.kgromov.repository.ContributorRepository;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@UIScope
public class ContributorForm extends FormLayout {
    private final ContributorRepository repository;
    private Contributor selectedContributor;

    /* Fields to edit properties in Contributor entity */
    private TextField login = new TextField("Login");
    private TextField firstName = new TextField("First name");
    private TextField lastName = new TextField("Last name");
    private IntegerField count = new IntegerField("Count");

    /* Action buttons */
    private Button save = new Button("Save", VaadinIcon.CHECK.create());
    private Button cancel = new Button("Cancel");
    private Button delete = new Button("Delete", VaadinIcon.TRASH.create());

    private Binder<Contributor> binder = new BeanValidationBinder<>(Contributor.class);
    private ChangeHandler changeHandler;

    @Autowired
    public ContributorForm(ContributorRepository repository) {
        this.repository = repository;

        // bind using naming convention
        binder.bindInstanceFields(this);
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        add(login, firstName, lastName, count, this.createButtonsLayout());
        setVisible(false);
    }

    private HorizontalLayout createButtonsLayout() {
        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        // wire action buttons to save, delete and reset
        save.addClickListener(e -> save());
        save.addClickShortcut(Key.ENTER);

        delete.addClickListener(e -> delete());
        delete.addClickShortcut(Key.DELETE);

        cancel.addClickListener(e -> editContributor(null));
        cancel.addClickShortcut(Key.ESCAPE);

        return new HorizontalLayout(save, delete, cancel);
    }

    void delete() {
        repository.delete(selectedContributor);
        changeHandler.onChange();
    }

    void save() {
        if (binder.isValid()) {
            repository.save(selectedContributor);
            changeHandler.onChange();
        }
    }

    public final void editContributor(Contributor contributor) {
        if (contributor == null) {
            this.close();
        } else {
            // Bind customer properties to similarly named fields
            selectedContributor = contributor;
            binder.setBean(selectedContributor);
            setVisible(true);
            login.focus();
        }
    }

    public void close() {
        binder.setBean(null);
        setVisible(false);
    }

    public interface ChangeHandler {
        void onChange();
    }

    public void setChangeHandler(ChangeHandler handler) {
        // ChangeHandler is notified when either save or delete is clicked
        changeHandler = handler;
    }
}
