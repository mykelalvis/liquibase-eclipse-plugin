package com.svcdelivery.liquibase.eclipse.internal.ui;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author nick
 * 
 */
public class ImportParser extends DefaultHandler {

	private IFile file;

	private ArrayList<IFile> imports;

	/**
	 * @param file
	 *            The file to parse.
	 */
	public ImportParser(final IFile file) {
		this.file = file;
		imports = new ArrayList<IFile>();
	}

	/**
	 * @return A list of direct imports.
	 */
	public final ArrayList<IFile> getImports() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(file.getLocation().toFile(), this);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imports;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if ("include".equals(qName)) {
			String included = attributes.getValue("file");
			if (included != null) {
				IPath path = new Path(included);
				IFile includedFile = file.getParent().getFile(path);
				imports.add(includedFile);
			}
		}
	}

}
