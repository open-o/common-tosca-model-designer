/*******************************************************************************
 * Copyright (c) 2014 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
/*
 * Modifications Copyright 2016 ZTE Corporation.
 */

package org.eclipse.winery.repository.resources.servicetemplates.boundarydefinitions.policies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.common.boundaryproperty.PolicyExtInfo;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources._support.IPersistable;
import org.eclipse.winery.repository.resources._support.collections.withoutid.EntityWithoutIdCollectionResource;

import com.sun.jersey.api.view.Viewable;

public class PoliciesResource extends EntityWithoutIdCollectionResource<PolicyResource, TPolicy> {

    public PoliciesResource(List<TPolicy> list, IPersistable res) {
        super(PolicyResource.class, TPolicy.class, list, res);
    }

    @Override
    public Viewable getHTML() {
        throw new IllegalStateException(
                "Not required: boundarydefinitions.jsp also includes the content of the Policy tab.");
    }

    @PUT
    public Response replaceAll(List<TPolicy> newList) {
        this.list.clear();
        for (TPolicy policy : newList) {
            this.list.add(policy);
        }
        return BackendUtils.persist(this.res);
    }

    @POST
    @Path("add/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPolicy(PolicyExtInfo info) {
        if (null == info || null == info.getName()) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        TPolicy policy = buildPolicy(info);
        this.list.add(policy);
        return BackendUtils.persist(this.res);
    }

    private TPolicy buildPolicy(PolicyExtInfo info) {
        return Utils.buildPolicyInfo(info);
    }

    @POST
    @Path("update/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePolicy(PolicyExtInfo info) {

        if (null == info || null == info.getName()) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        rmPolicy(info.getName());

        TPolicy policy = buildPolicy(info);
        this.list.add(policy);
        return BackendUtils.persist(this.res);
    }

    private void rmPolicy(String name) {
        for (Iterator<TPolicy> it = this.list.iterator(); it.hasNext();) {
            if (name.equals(it.next().getName())) {
                it.remove();
                return;
            }
        }
    }

    @DELETE
    @Path("delete/")
    public Response deletePolicy(@QueryParam("name") String name) {
        if (null == name) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        rmPolicy(name);
        return BackendUtils.persist(this.res);
    }

    @GET
    @Path("query/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PolicyExtInfo> query(@QueryParam("name") String name) {
        List<TPolicy> policies = new ArrayList<TPolicy>();
        if (null == name) {
            policies.addAll(list);
        } else {
            for (TPolicy policy : this.list) {
                if (name.equals(policy.getName())) {
                    policies.add(policy);
                    break;
                }
            }
        }
        return convert(policies);
    }

    private List<PolicyExtInfo> convert(List<TPolicy> policies) {
        List<PolicyExtInfo> result = new ArrayList<PolicyExtInfo>();
        for (TPolicy policy : policies) {
            result.add(Utils.buildTPolicy(policy));
        }
        return result;
    }
}
