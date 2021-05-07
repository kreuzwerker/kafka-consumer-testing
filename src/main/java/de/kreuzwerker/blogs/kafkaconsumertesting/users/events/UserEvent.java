package de.kreuzwerker.blogs.kafkaconsumertesting.users.events;

import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.SchemaStore;
import org.apache.avro.specific.SpecificData;

/**
 * This class would normally end up in generated sources, this is just a decompiled class for demonstration purpose!
 */
@org.apache.avro.specific.AvroGenerated
public class UserEvent extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord{
    private static final long serialVersionUID = 5665983904606858982L;
  public static final org.apache.avro.Schema SCHEMA$ =
      new org.apache.avro.Schema.Parser()
          .parse(
              "{\"type\": \"record\",\"name\": \"UserEvent\",\"namespace\":\"de.kreuzwerker.blogs.kafkaconsumertesting.users.events\",\"fields\": [{\"name\": \"id\",\"type\": {\"type\": \"string\",\"avro.java.string\": \"String\"},\"logicalType\": \"UUID\"},{\"name\": \"firstName\",\"type\": [{\"type\": \"string\",\"avro.java.string\": \"String\"},\"null\"]},{\"name\": \"lastName\",\"type\": [{\"type\": \"string\",\"avro.java.string\": \"String\"},\"null\"]}]}");

    public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

    private static SpecificData MODEL$ = new SpecificData();
    static {
        MODEL$.addLogicalTypeConversion(new org.apache.avro.data.TimeConversions.DateConversion());
        MODEL$.addLogicalTypeConversion(new org.apache.avro.data.TimeConversions.TimestampMillisConversion());
    }

    private static final BinaryMessageEncoder<UserEvent> ENCODER =
            new BinaryMessageEncoder<UserEvent>(MODEL$, SCHEMA$);

    private static final BinaryMessageDecoder<UserEvent> DECODER =
            new BinaryMessageDecoder<UserEvent>(MODEL$, SCHEMA$);

    public static BinaryMessageEncoder<UserEvent> getEncoder() {
        return ENCODER;
    }

    public static BinaryMessageDecoder<UserEvent> getDecoder() {
        return DECODER;
    }

    public static BinaryMessageDecoder<UserEvent> createDecoder(SchemaStore resolver) {
        return new BinaryMessageDecoder<UserEvent>(MODEL$, SCHEMA$, resolver);
    }

    public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
        return ENCODER.encode(this);
    }

    public static UserEvent fromByteBuffer(
            java.nio.ByteBuffer b) throws java.io.IOException {
        return DECODER.decode(b);
    }

    private java.lang.String id;
    private java.lang.String firstName;
    private java.lang.String lastName;

    public UserEvent() {}

    public UserEvent(java.lang.String id, java.lang.String firstName, java.lang.String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
    public org.apache.avro.Schema getSchema() { return SCHEMA$; }
    // Used by DatumWriter.  Applications should not call.
    public java.lang.Object get(int field$) {
        switch (field$) {
            case 0: return id;
            case 1: return firstName;
            case 2: return lastName;
            default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
        }
    }

    // Used by DatumReader.  Applications should not call.
    @SuppressWarnings(value="unchecked")
    public void put(int field$, java.lang.Object value$) {
        switch (field$) {
            case 0: id = value$ != null ? value$.toString() : null; break;
            case 1: firstName = value$ != null ? value$.toString() : null; break;
            case 2: lastName = value$ != null ? value$.toString() : null; break;
            default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
        }
    }

    public java.lang.String getId() {
        return id;
    }
    public void setId(java.lang.String value) {
        this.id = value;
    }

    public java.lang.String getFirstName() {
        return firstName;
    }
    public void setFirstName(java.lang.String value) {
        this.firstName = value;
    }

    public java.lang.String getLastName() {
        return lastName;
    }
    public void setLastName(java.lang.String value) {
        this.lastName = value;
    }

    public static de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent.Builder newBuilder() {
        return new de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent.Builder();
    }

    /**
     * Creates a new MachineEvent RecordBuilder by copying an existing Builder.
     * @param other The existing builder to copy.
     * @return A new MachineEvent RecordBuilder
     */
    public static de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent.Builder newBuilder(de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent.Builder other) {
        if (other == null) {
            return new de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent.Builder();
        } else {
            return new de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent.Builder(other);
        }
    }

    /**
     * Creates a new MachineEvent RecordBuilder by copying an existing MachineEvent instance.
     * @param other The existing instance to copy.
     * @return A new MachineEvent RecordBuilder
     */
    public static de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent.Builder newBuilder(de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent other) {
        if (other == null) {
            return new de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent.Builder();
        } else {
            return new de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent.Builder(other);
        }
    }

    @org.apache.avro.specific.AvroGenerated
    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<UserEvent>
            implements org.apache.avro.data.RecordBuilder<UserEvent> {

        private java.lang.String id;
        private java.lang.String firstName;
        private java.lang.String lastName;

        /** Creates a new Builder */
        private Builder() {
            super(SCHEMA$);
        }

        private Builder(de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent.Builder other) {
            super(other);
            if (isValidValue(fields()[0], other.id)) {
                this.id = data().deepCopy(fields()[0].schema(), other.id);
                fieldSetFlags()[0] = other.fieldSetFlags()[0];
            }
            if (isValidValue(fields()[1], other.firstName)) {
                this.firstName = data().deepCopy(fields()[1].schema(), other.firstName);
                fieldSetFlags()[1] = other.fieldSetFlags()[1];
            }
            if (isValidValue(fields()[2], other.lastName)) {
                this.lastName = data().deepCopy(fields()[2].schema(), other.lastName);
                fieldSetFlags()[2] = other.fieldSetFlags()[2];
            }
        }

        private Builder(de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent other) {
            super(SCHEMA$);
            if (isValidValue(fields()[0], other.id)) {
                this.id = data().deepCopy(fields()[0].schema(), other.id);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.firstName)) {
                this.id = data().deepCopy(fields()[1].schema(), other.firstName);
                fieldSetFlags()[1] = true;
            }
            if (isValidValue(fields()[2], other.lastName)) {
                this.id = data().deepCopy(fields()[2].schema(), other.lastName);
                fieldSetFlags()[2] = true;
            }
        }

        public java.lang.String getId() {
            return id;
        }

        public de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent.Builder setId(java.lang.String value) {
            validate(fields()[0], value);
            this.id = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        public boolean hasId() {
            return fieldSetFlags()[0];
        }


        public de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent.Builder clearId() {
            id = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        public java.lang.String getFirstName() {
            return firstName;
        }

        public de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent.Builder setFirstName(java.lang.String value) {
            validate(fields()[1], value);
            this.firstName = value;
            fieldSetFlags()[1] = true;
            return this;
        }

        public boolean hasFirstName() {
            return fieldSetFlags()[1];
        }


        public de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent.Builder clearFirstName() {
            firstName = null;
            fieldSetFlags()[2] = false;
            return this;
        }

        public java.lang.String getLastName() {
            return lastName;
        }


        public de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent.Builder setLastName(java.lang.String value) {
            validate(fields()[2], value);
            this.lastName = value;
            fieldSetFlags()[2] = true;
            return this;
        }

        public boolean hasLastName() {
            return fieldSetFlags()[2];
        }


        public de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent.Builder clearLastName() {
            firstName = null;
            fieldSetFlags()[2] = false;
            return this;
        }



        @Override
        @SuppressWarnings("unchecked")
        public UserEvent build() {
            try {
                UserEvent record = new UserEvent();
                record.id = fieldSetFlags()[0] ? this.id : (java.lang.String) defaultValue(fields()[0]);
                record.firstName = fieldSetFlags()[1] ? this.firstName : (java.lang.String) defaultValue(fields()[1]);
                record.lastName = fieldSetFlags()[2] ? this.lastName : (java.lang.String) defaultValue(fields()[2]);
                return record;
            } catch (org.apache.avro.AvroMissingFieldException e) {
                throw e;
            } catch (java.lang.Exception e) {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumWriter<UserEvent>
            WRITER$ = (org.apache.avro.io.DatumWriter<UserEvent>)MODEL$.createDatumWriter(SCHEMA$);

    @Override public void writeExternal(java.io.ObjectOutput out)
            throws java.io.IOException {
        WRITER$.write(this, SpecificData.getEncoder(out));
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumReader<UserEvent>
            READER$ = (org.apache.avro.io.DatumReader<UserEvent>)MODEL$.createDatumReader(SCHEMA$);

    @Override public void readExternal(java.io.ObjectInput in)
            throws java.io.IOException {
        READER$.read(this, SpecificData.getDecoder(in));
    }

}
