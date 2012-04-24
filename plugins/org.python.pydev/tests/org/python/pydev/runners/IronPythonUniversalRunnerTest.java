/**
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.txt included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package org.python.pydev.runners;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.python.pydev.core.IPythonNature;
import org.python.pydev.core.TestDependent;
import org.python.pydev.core.Tuple;
import org.python.pydev.editor.codecompletion.IronPythonCodeCompletionTestsBase;
import org.python.pydev.runners.UniversalRunner.AbstractRunner;

public class IronPythonUniversalRunnerTest extends IronPythonCodeCompletionTestsBase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(IronPythonUniversalRunnerTest.class);
    }

    
    public void testUniversalRunnerWithIronPython() throws Exception {
		AbstractRunner runner = UniversalRunner.getRunner(nature);
		assertEquals(nature.getInterpreterType(), IPythonNature.INTERPRETER_TYPE_IRONPYTHON);
		Tuple<String, String> output = runner.runCodeAndGetOutput(
				"import sys\nprint 'test'\nprint >> sys.stderr, 'err'", null, null, new NullProgressMonitor());
		assertEquals("test", output.o1.trim());
		assertEquals("err", output.o2.trim());
		
		Tuple<Process, String> createProcess = 
			runner.createProcess(TestDependent.TEST_PYSRC_LOC+"universal_runner_test.py", null, null, new NullProgressMonitor());
		output = SimpleRunner.getProcessOutput(createProcess.o1, "", new NullProgressMonitor(), "utf-8");
		assertEquals("stdout", output.o1.trim());
		assertEquals("stderr", output.o2.trim());
		
	}
    
}
