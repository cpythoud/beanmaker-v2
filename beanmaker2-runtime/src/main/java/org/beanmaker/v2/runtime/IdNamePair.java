package org.beanmaker.v2.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IdNamePair implements Comparable<IdNamePair> {

	private final String id;
	private final String name;
	private final boolean disabled;

	public IdNamePair(int id, String name) {
		this(id, name, false);
	}

	public IdNamePair(long id, String name) {
		this(id, name, false);
	}

	public IdNamePair(String id, String name) {
		this(id, name, false);
	}
	
	public IdNamePair(int id, String name, boolean disabled) {
		if (id < 0)
			throw new IllegalArgumentException("id must be zero or positive");
		
		this.id = Integer.toString(id);
		this.name = name;
		this.disabled = disabled;
	}
	
	public IdNamePair(long id, String name, boolean disabled) {
		if (id < 0)
			throw new IllegalArgumentException("id must be zero or positive");
		
		this.id = Long.toString(id);
		this.name = name;
		this.disabled = disabled;
	}
	
	public IdNamePair(String id, String name, boolean disabled) {
		this.id = id;
		this.name = name;
		this.disabled = disabled;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public boolean isDisabled() {
		return disabled;
	}

	// ! Assumes only one "please select ..." element with id "0", throws an IllegalStateException otherwise.
	@Override
	public int compareTo(IdNamePair idNamePair) {
		if (id.equals("0") && idNamePair.id.equals("0"))
			throw new IllegalStateException("More than one 'please select' field in IdNamePair collection.");

		if (id.equals("0"))
			return -1;
		if (idNamePair.id.equals("0"))
			return 1;

		return name.compareTo(idNamePair.name);
	}

	// * DbBeanLocalization & label name based functions

	public static <T extends DbBeanInterface> List<IdNamePair> getPairs(
			List<T> beans,
			DbBeanLocalization localization)
	{
		return getPairs(beans, localization.getLanguage());
	}

	public static <T extends DbBeanInterface> List<IdNamePair> getPairs(
			List<T> beans,
			DbBeanLocalization localization,
			String noSelectionLabelName)
	{
		return getPairs(beans, localization.getLanguage(), localization.getLabel(noSelectionLabelName));
	}

	public static <T extends DbBeanInterface> List<IdNamePair> getPairs(
			List<T> beans,
			DbBeanLocalization localization,
			String noSelectionLabelName,
			boolean sortOnName)
	{
		return getPairs(beans, localization.getLanguage(), localization.getLabel(noSelectionLabelName), sortOnName);
	}

	public static <T extends DbBeanInterface> List<IdNamePair> getPairs(
			List<T> beans,
			DbBeanLanguage dbBeanLanguage)
	{
		return getPairs(beans, dbBeanLanguage, null);
	}

	public static <T extends DbBeanInterface> List<IdNamePair> getPairs(
			List<T> beans,
			DbBeanLanguage dbBeanLanguage,
			String noSelectionText)
	{
		return getPairs(beans, dbBeanLanguage, noSelectionText, false);
	}

	public static <T extends DbBeanInterface> List<IdNamePair> getPairs(
			List<T> beans,
			DbBeanLanguage dbBeanLanguage,
			String noSelectionText,
			boolean sortOnName)
	{
		List<IdNamePair> pairs = new ArrayList<>();

		for (DbBeanInterface bean: beans)
			pairs.add(new IdNamePair(bean.getId(), bean.getNameForIdNamePairsAndTitles(dbBeanLanguage)));

		if (sortOnName)
			Collections.sort(pairs);

		if (noSelectionText != null)
			pairs.add(0, new IdNamePair(0, noSelectionText));

		return pairs;
	}
}
