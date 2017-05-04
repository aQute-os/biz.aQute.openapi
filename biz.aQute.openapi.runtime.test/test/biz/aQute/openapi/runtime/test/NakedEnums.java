package biz.aQute.openapi.runtime.test;

import org.junit.Test;

import gen.nakedenum.NakedenumBase;
import gen.nakedenum.NakedenumBase.NakedEnum;

public class NakedEnums {

	@SuppressWarnings("unused")
	@Test
	void testNakedEnum() {
		NakedEnum a = NakedenumBase.NakedEnum.A;
		NakedEnum b = NakedenumBase.NakedEnum.B;
	}
}
