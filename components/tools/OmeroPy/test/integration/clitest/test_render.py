#!/usr/bin/env python
# -*- coding: utf-8 -*-

#
# Copyright (C) 2015-2016 University of Dundee & Open Microscopy Environment.
# All rights reserved.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

import json
import pytest

from omero.plugins.render import RenderControl
from omero.cli import NonZeroReturnCode
from test.integration.clitest.cli import CLITest
from omero.gateway import BlitzGateway


# TODO: rdefid, tbid
SUPPORTED = {
    "idonly": "-1",
    "imageid": "Image:-1",
    "plateid": "Plate:-1",
    "screenid": "Screen:-1",
}


class TestRender(CLITest):

    def setup_method(self, method):
        super(TestRender, self).setup_method(method)
        self.cli.register("render", RenderControl, "TEST")
        self.args += ["render"]

    def create_image(self):
        self.gw = BlitzGateway(client_obj=self.client)
        self.plates = []
        for plate in self.importPlates(fields=2, sizeC=4):
            self.plates.append(self.gw.getObject("Plate", plate.id.val))
        # Now pick the first Image
        self.imgobj = list(self.plates[0].listChildren())[0].getImage(index=0)
        self.idonly = "%s" % self.imgobj.id
        self.imageid = "Image:%s" % self.imgobj.id
        self.plateid = "Plate:%s" % self.plates[0].id
        self.screenid = "Screen:%s" % self.plates[0].getParent().id
        # And another one as the source for copies
        self.source = list(self.plates[0].listChildren())[0].getImage(index=1)
        self.source = "Image:%s" % self.source.id
        # And for all the images, pre-load a thumbnail
        for p in self.plates:
            for w in p.listChildren():
                for i in range(w.countWellSample()):
                    w.getImage(index=i).getThumbnail(
                        size=(96,), direct=False)

    def get_target_imageids(self, target):
        if target in (self.idonly, self.imageid):
            return [self.idonly]
        if target == self.plateid:
            imgs = []
            for w in self.plates[0].listChildren():
                imgs.extend([w.getImage(0).id, w.getImage(1).id])
            return imgs
        if target == self.screenid:
            imgs = []
            for s in self.plates:
                for w in self.plates[0].listChildren():
                    imgs.extend([w.getImage(0).id, w.getImage(1).id])
            return imgs
        raise Exception('Unknown target: %s' % target)

    def get_render_def(self):
        channels = {}
        channels[1] = {
            'label': self.uuid(),
            'color': '123456',
            'min': 11,
            'max': 22,
        }
        channels[2] = {
            'label': self.uuid(),
            'color': '789ABC',
            'min': 33,
            'max': 44,
        }
        channels[3] = {
            'label': self.uuid(),
            'color': 'DEF012',
            'min': 55,
            'max': 66,
        }
        channels[4] = {
            'label': self.uuid(),
            'color': '345678',
            'min': 77,
            'max': 88,
        }
        return {'channels': channels}

    def assert_channel_rdef(self, channel, rdef):
        assert channel.getLabel() == rdef['label']
        assert channel.getColor().getHtml() == rdef['color']
        assert channel.getWindowStart() == rdef['min']
        assert channel.getWindowEnd() == rdef['max']

    # rendering tests
    # ========================================================================

    @pytest.mark.parametrize('targetName', sorted(SUPPORTED.keys()))
    def testNonExistingImage(self, targetName, tmpdir):
        target = SUPPORTED[targetName]
        self.args += ["info", target]
        with pytest.raises(NonZeroReturnCode):
            self.cli.invoke(self.args, strict=True)

    @pytest.mark.parametrize('targetName', sorted(SUPPORTED.keys()))
    def testInfo(self, targetName, tmpdir):
        self.create_image()
        target = getattr(self, targetName)
        self.args += ["info", target]
        self.cli.invoke(self.args, strict=True)

    @pytest.mark.parametrize('targetName', sorted(SUPPORTED.keys()))
    def testCopy(self, targetName, tmpdir):
        self.create_image()
        target = getattr(self, targetName)
        self.args += ["copy", self.source, target]
        self.cli.invoke(self.args, strict=True)

    @pytest.mark.parametrize('targetName', sorted(SUPPORTED.keys()))
    def testEdit(self, targetName, tmpdir):
        self.create_image()
        rd = self.get_render_def()
        rdfile = tmpdir.join('render-test-edit.json')
        # Should work with json and yaml, but yaml is an optional dependency
        rdfile.write(json.dumps(rd))
        target = getattr(self, targetName)
        self.args += ["edit", target, str(rdfile)]
        self.cli.invoke(self.args, strict=True)

        iids = self.get_target_imageids(target)
        print 'Got %d images' % len(iids)
        gw = BlitzGateway(client_obj=self.client)
        for iid in iids:
            # Get the updated object
            img = gw.getObject('Image', iid)
            channels = img.getChannels()
            assert len(channels) == 4
            for c in xrange(len(channels)):
                self.assert_channel_rdef(channels[c], rd['channels'][c + 1])
