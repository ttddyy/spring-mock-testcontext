package net.ttddyy.mocktestcontext;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manager to keep MockInfo
 *
 * @author Tadaya Tsuyukubo
 */
public class MockInfoManager {

	private Set<MockInfo> mockInfoSet = new HashSet<MockInfo>();

	public MockInfo createMockInfo(String beanName, Class<?> type) {
		MockInfo mockInfo = new MockInfo(beanName, type);
		mockInfoSet.add(mockInfo);
		return mockInfo;
	}

	public MockInfo getByBeanName(String beanName) {
		Assert.notNull(beanName);

		// beanName is unique
		for (MockInfo mockInfo : mockInfoSet) {
			if (beanName.equals(mockInfo.getBeanName())) {
				return mockInfo;
			}
		}
		return null;
	}

	public List<MockInfo> getByTypeExceptMe(MockInfo mockInfo) {
		Assert.notNull(mockInfo);
		Assert.notNull(mockInfo.getType());

		List<MockInfo> result = new ArrayList<MockInfo>();
		for (MockInfo candidate : mockInfoSet) {
			if (mockInfo.equals(candidate)) {
				continue;
			}
			if (mockInfo.getType().equals(candidate.getType())) {
				result.add(candidate);
			}
		}
		return result;
	}

	public MockInfo getByTypeAndQualifierNameExceptMe(MockInfo mockInfo) {
		Assert.notNull(mockInfo);
		Assert.notNull(mockInfo.getType());
		Assert.notNull(mockInfo.getQualifierName());

		for (MockInfo candidate : mockInfoSet) {
			if (mockInfo.equals(candidate)) {
				continue;
			}
			if (mockInfo.getType().equals(candidate.getType()) &&
					mockInfo.getQualifierName().equals(candidate.getQualifierName())) {
				return candidate;
			}
		}
		return null;
	}

	public boolean remove(MockInfo mockInfo) {
		return mockInfoSet.remove(mockInfo);
	}

}
