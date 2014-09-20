package net.ttddyy.mocktestcontext;

/**
 * Keep mock bean info.
 *
 * @author Tadaya Tsuyukubo
 */
public class MockInfo {
	private String beanName;
	private Class<?> type;
	private String qualifierName;

	public MockInfo(String beanName, Class<?> type) {
		this.beanName = beanName;
		this.type = type;
	}

	public String getBeanName() {
		return beanName;
	}

	public Class<?> getType() {
		return type;
	}

	public String getQualifierName() {
		return qualifierName;
	}

	public void setQualifierName(String qualifierName) {
		this.qualifierName = qualifierName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MockInfo)) return false;

		MockInfo mockInfo = (MockInfo) o;

		if (!beanName.equals(mockInfo.beanName)) return false;
		if (qualifierName != null ? !qualifierName.equals(mockInfo.qualifierName) : mockInfo.qualifierName != null)
			return false;
		if (!type.equals(mockInfo.type)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = beanName.hashCode();
		result = 31 * result + type.hashCode();
		result = 31 * result + (qualifierName != null ? qualifierName.hashCode() : 0);
		return result;
	}
}
