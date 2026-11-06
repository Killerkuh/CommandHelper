package de.Simon.CommandHelper.commads.Projekt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface BinarySerializable {

    void write(DataOutputStream out) throws IOException;

    void read(DataInputStream in) throws IOException;
}
