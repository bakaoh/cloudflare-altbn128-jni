/*
 * This file is part of RskJ
 * Copyright (C) 2019 RSK Labs Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.bakaoh.altbn128.current;

import com.bakaoh.altbn128.PrecompiledContract;
import org.ethereum.crypto.altbn128.BN128;
import org.ethereum.crypto.altbn128.BN128Fp;
import org.ethereum.crypto.altbn128.Fp;

import static com.bakaoh.altbn128.current.ByteUtil.*;

/**
 * Computes point addition on Barreto–Naehrig curve.
 * See {@link BN128Fp} for details<br/>
 * <br/>
 *
 * input data[]:<br/>
 * two points encoded as (x, y), where x and y are 32-byte left-padded integers,<br/>
 * if input is shorter than expected, it's assumed to be right-padded with zero bytes<br/>
 * <br/>
 *
 * output:<br/>
 * resulting point (x', y'), where x and y encoded as 32-byte left-padded integers<br/>
 *
 */

/**
 * @author Sebastian Sicardi
 * @since 10.09.2019
 */
public class BN128Addition extends PrecompiledContract {

    private static byte[] encodeRes(byte[] w1, byte[] w2) {

        byte[] res = new byte[64];

        w1 = stripLeadingZeroes(w1);
        w2 = stripLeadingZeroes(w2);

        System.arraycopy(w1, 0, res, 32 - w1.length, w1.length);
        System.arraycopy(w2, 0, res, 64 - w2.length, w2.length);

        return res;
    }

    @Override
    public long getGasForData(byte[] data) {
        return 500;
    }

    @Override
    public byte[] execute(byte[] data) {

        if (data == null) {
            data = EMPTY_BYTE_ARRAY;
        }

        byte[] x1 = parseWord(data, 0);
        byte[] y1 = parseWord(data, 1);

        byte[] x2 = parseWord(data, 2);
        byte[] y2 = parseWord(data, 3);

        BN128<Fp> p1 = BN128Fp.create(x1, y1);

        if (p1 == null) {
            return EMPTY_BYTE_ARRAY;
        }

        BN128<Fp> p2 = BN128Fp.create(x2, y2);
        if (p2 == null) {
            return EMPTY_BYTE_ARRAY;
        }

        BN128<Fp> res = p1.add(p2).toEthNotation();

        return encodeRes(res.x().bytes(), res.y().bytes());
    }

}
