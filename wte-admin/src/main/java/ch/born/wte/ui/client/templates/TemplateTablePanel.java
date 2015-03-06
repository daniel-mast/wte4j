package ch.born.wte.ui.client.templates;

import static ch.born.wte.ui.client.Application.LABELS;
import static ch.born.wte.ui.client.Application.RESOURCES;

import java.util.Date;

import ch.born.wte.ui.shared.TemplateDto;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasData;

public class TemplateTablePanel extends Composite implements
		TemplateListDisplay {

	private static TemplateTablePanelUiBInder uiBinder = GWT
			.create(TemplateTablePanelUiBInder.class);

	@UiField
	FlowPanel tablePanel;

	@UiField(provided = true)
	CellTable<TemplateDto> templateTable = new CellTable<TemplateDto>();

	private Column<TemplateDto, String> nameColumn;
	private Column<TemplateDto, Date> editedAtColumn;
	private Column<TemplateDto, String> editorColumn;
	private Column<TemplateDto, ?> actionColumn;

	@UiField(provided = true)
	AbstractPager tablePager = new SimplePager();

	private DateTimeFormat timeStampFormat = DateTimeFormat
			.getFormat(PredefinedFormat.DATE_TIME_SHORT);

	private PopupPanel contextPanel;
	private TemplateContextMenu templateContextMenu;

	private TextColumn<TemplateDto> statusColumn;

	public TemplateTablePanel() {
		initWidget(uiBinder.createAndBindUi(this));
		initContextMenu();
		initTemplateTable();

	}

	private void initContextMenu() {

		templateContextMenu = new TemplateContextMenu();
		contextPanel = new PopupPanel(true);
		contextPanel.add(templateContextMenu);

		ClickHandler clickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				contextPanel.hide();

			}
		};

		contextPanel.addHandler(clickHandler, ClickEvent.getType());
		templateContextMenu.addHandler(clickHandler, ClickEvent.getType());
	}

	private void initTemplateTable() {
		templateTable.addStyleName("templates-table");
		tablePager.setDisplay(templateTable);

		initNameColumn();
		initEditedAtColumn();
		initEditorColumn();
		initStatusColumn();
		initActionColumn();
	}

	private void initNameColumn() {
		nameColumn = new TextColumn<TemplateDto>() {

			@Override
			public String getValue(TemplateDto template) {
				return template.getDocumentName();
			}

		};
		nameColumn.setCellStyleNames("templates-name-cell");
		addColumntoTemplateTable(nameColumn, LABELS.templateDocumentName());
	}

	private void initEditedAtColumn() {
		DateCell dateCell = new DateCell(timeStampFormat);
		editedAtColumn = new Column<TemplateDto, Date>(dateCell) {

			@Override
			public Date getValue(TemplateDto object) {
				return object.getUpdatedAt();
			}
		};
		editedAtColumn.setCellStyleNames("templates-editedAt-cell");
		editedAtColumn.setHorizontalAlignment(Column.ALIGN_RIGHT);
		addColumntoTemplateTable(editedAtColumn, LABELS.templateEditedAt());

	}

	private void initEditorColumn() {
		editorColumn = new TextColumn<TemplateDto>() {

			@Override
			public String getValue(TemplateDto template) {
				return template.getEditor().getDisplayName();
			}
		};
		editorColumn.setCellStyleNames("templates-editor-cell");
		editorColumn.setHorizontalAlignment(Column.ALIGN_LEFT);
		addColumntoTemplateTable(editorColumn, LABELS.templateEditor());
	}

	private void initActionColumn() {

		ImageResourceCell imageResourceCell = new ImageResourceCell();
		PopupCell<ImageResource> popupCell = new PopupCell<ImageResource>(
				contextPanel, imageResourceCell);
		actionColumn = new Column<TemplateDto, ImageResource>(
				popupCell) {
			@Override
			public ImageResource getValue(TemplateDto object) {
				return RESOURCES.contextMenuIcon();
			}
		};
		actionColumn.setCellStyleNames("templates-action-cell");
		addColumntoTemplateTable(actionColumn, "");
	}
	
	private void initStatusColumn() {
		statusColumn = new TextColumn<TemplateDto>() {

			@Override
			public String getValue(TemplateDto template) {
				String lockingUserName = "";
				if (template.getLockingUser() != null) {
					lockingUserName = template.getLockingUser().getDisplayName();
				}
				return lockingUserName;
			}
		};
		statusColumn.setCellStyleNames("locking-user-cell");
		addColumntoTemplateTable(statusColumn, LABELS.templateLockingUser());
	}
	
	private void addColumntoTemplateTable(Column<TemplateDto, ?> column, String headerText) {
		Header<String> header = new TextHeader(headerText);
		header.setHeaderStyleNames("templates-header-cell");
		templateTable.addColumn(column, header);
	}	
	
	@Override
	public HasData<TemplateDto> getDataContainer() {
		return templateTable;
	}

	@Override
	public void setDowndLoadCommand(ScheduledCommand command) {
		ScheduledCommand commandWithCloseContextMenu = decorateCommandWithCloseContextMenu(command);
		templateContextMenu.getDownloadAction().setScheduledCommand(commandWithCloseContextMenu);
	}

	@Override
	public void setUpdateCommand(ScheduledCommand command) {
		ScheduledCommand commandWithCloseContextMenu = decorateCommandWithCloseContextMenu(command);
		templateContextMenu.getUpdateAction().setScheduledCommand(commandWithCloseContextMenu);

	}

	@Override
	public void setUnlockCommand(ScheduledCommand command) {
		ScheduledCommand commandWithCloseContextMenu = decorateCommandWithCloseContextMenu(command);
		templateContextMenu.getUnlockAction().setScheduledCommand(commandWithCloseContextMenu);

	}

	@Override
	public void setLockCommand(ScheduledCommand command) {
		ScheduledCommand commandWithCloseContextMenu = decorateCommandWithCloseContextMenu(command);
		templateContextMenu.getLockAction().setScheduledCommand(commandWithCloseContextMenu);

	}

	@Override
	public void setDeleteCommand(ScheduledCommand command) {
		ScheduledCommand decoretedCommandWithCloseContextMenu = decorateCommandWithCloseContextMenu(command);
		templateContextMenu.getDeleteAction().setScheduledCommand(decoretedCommandWithCloseContextMenu);

	}

	private ScheduledCommand decorateCommandWithCloseContextMenu(final ScheduledCommand command) {
		return new ScheduledCommand() {
			@Override
			public void execute() {
				contextPanel.hide();
				command.execute();
			}
		};
	}

	interface TemplateTablePanelUiBInder extends
			UiBinder<Widget, TemplateTablePanel> {
	}

}
