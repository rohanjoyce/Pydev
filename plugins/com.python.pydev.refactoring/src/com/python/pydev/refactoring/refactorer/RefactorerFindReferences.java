/**
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.txt included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
/*
 * Created on Dec 9, 2006
 * @author Fabio
 */
package com.python.pydev.refactoring.refactorer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.python.pydev.core.IPythonNature;
import org.python.pydev.core.MisconfigurationException;
import org.python.pydev.core.ModulesKey;
import org.python.pydev.core.REF;
import org.python.pydev.core.Tuple;
import org.python.pydev.core.log.Log;
import org.python.pydev.editor.refactoring.RefactoringRequest;

import com.python.pydev.analysis.additionalinfo.AbstractAdditionalTokensInfo;
import com.python.pydev.analysis.additionalinfo.AdditionalProjectInterpreterInfo;

/**
 * Refactorer used to find the references given some refactoring request.
 * 
 * @author Fabio
 */
public class RefactorerFindReferences {
    
    /**
     * If this field is not null, the return will be forced without actually doing
     * a search in files.
     * 
     * This is intended to help in testing features that depend on the search.
     */
    public static ArrayList<Tuple<List<ModulesKey>, IPythonNature>> FORCED_RETURN;


    /**
     * Find the references that may have the text we're looking for.
     * 
     * @param request the request with the info for the find
     * @return an array of IFile with the files that may have the references we're
     * interested about (note that those may not actually contain the matches we're
     * interested in -- it is just a helper to refine our search).
     */
    public ArrayList<Tuple<List<ModulesKey>, IPythonNature>> findPossibleReferences(RefactoringRequest request) {
        if(FORCED_RETURN != null){
            ArrayList<Tuple<List<ModulesKey>, IPythonNature>> ret = new ArrayList<Tuple<List<ModulesKey>, IPythonNature>>();
            
            for(Tuple<List<ModulesKey>, IPythonNature> f: FORCED_RETURN){
                //only for testing purposes
                for(ModulesKey k:f.o1){
                    String object = REF.getFileContents(k.file);
                    if(object.indexOf(request.initialName) != -1){
                        ret.add(new Tuple<List<ModulesKey>, IPythonNature>(Arrays.asList(k), f.o2));
                    }
                }
            }
            return ret;
        }
        
        ArrayList<Tuple<List<ModulesKey>, IPythonNature>> l = new ArrayList<Tuple<List<ModulesKey>, IPythonNature>>();
        
        try {
            IProject project = request.nature.getProject();
            if(project == null){
            	return l;
            }

            
            ArrayList<Tuple<List<ModulesKey>, IPythonNature>> ret = new ArrayList<Tuple<List<ModulesKey>, IPythonNature>>();
            try {
                List<Tuple<AbstractAdditionalTokensInfo, IPythonNature>> infoAndNature = 
                    AdditionalProjectInterpreterInfo.getAdditionalInfoAndNature(request.nature, false, true, true);

                for (Tuple<AbstractAdditionalTokensInfo, IPythonNature> tuple : infoAndNature) {
                    if(tuple.o1 != null && tuple.o2 != null){
                        List<ModulesKey> modulesWithToken = tuple.o1.getModulesWithToken(
                                request.initialName, request.getMonitor());
                        
                        ret.add(new Tuple<List<ModulesKey>, IPythonNature>(modulesWithToken, tuple.o2));
                    }
                }
            } catch (MisconfigurationException e) {
                Log.log(e);
            }
            
        } catch (Exception e) {
            Log.log(e);
        }
        return l;
    }

}
