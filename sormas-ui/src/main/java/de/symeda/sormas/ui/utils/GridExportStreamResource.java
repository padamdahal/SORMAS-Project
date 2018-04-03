package de.symeda.sormas.ui.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.opencsv.CSVWriter;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Property;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;

@SuppressWarnings("serial")
public class GridExportStreamResource extends StreamResource {

	public GridExportStreamResource(Indexed container, List<Column> columns, String tempFilePrefix, String filename, String... ignoredPropertyIds) {
		super(new StreamSource() {
			@Override
			public InputStream getStream() {
				try {
					List<String> ignoredPropertyIdsList = Arrays.asList(ignoredPropertyIds);
					columns.removeIf(c -> c.isHidden());
					columns.removeIf(c -> ignoredPropertyIdsList.contains(c.getPropertyId()));
					Collection<?> itemIds = container.getItemIds();

					List<List<String>> exportedRows = new ArrayList<>();
					
					List<String> headerRow = new ArrayList<>();
					columns.forEach(c -> {
						headerRow.add(c.getHeaderCaption());
					});
					exportedRows.add(headerRow);
					
					itemIds.forEach(i -> {
						List<String> row = new ArrayList<>();
						columns.forEach(c -> {
							Property<?> property = container.getItem(i).getItemProperty(c.getPropertyId());
							if (property.getValue() != null) {
								if (property.getType() == Date.class) {
									row.add(DateHelper.formatDateTime((Date) property.getValue()));
								} else if (property.getType() == Boolean.class) {
									if ((Boolean) property.getValue() == true) {
										row.add(I18nProperties.getEnumCaption(YesNoUnknown.YES));
									} else
										row.add(I18nProperties.getEnumCaption(YesNoUnknown.NO));
								} else {
									row.add(property.getValue().toString());
								}
							} else {
								row.add("");
							}
						});

						exportedRows.add(row);
					});

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8.name());
					CSVWriter writer = CSVUtils.createCSVWriter(osw);
					exportedRows.forEach(r -> {
						writer.writeNext(r.toArray(new String[r.size()]));
					});

					osw.flush();
					baos.flush();
					
					return new BufferedInputStream(new ByteArrayInputStream(baos.toByteArray()));
				} catch (IOException e) {
					// TODO This currently requires the user to click the "Export" button again or reload the page as the UI
					// is not automatically updated; this should be changed once Vaadin push is enabled (see #516)
					new Notification("Export failed", "There was an error trying to provide the data to export. Please contact an admin and inform them about this issue.", Type.ERROR_MESSAGE, false).show(Page.getCurrent());
					return null;
				}
			}
		}, filename);
		setMIMEType("text/csv");
		setCacheTime(0);
	}
	
}