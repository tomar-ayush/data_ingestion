import { Table, Import, Download as Export, SlidersHorizontal, Check } from "lucide-react";

const features = [
  {
    icon: <Import size={28} className="text-blue-500" />,
    title: "ClickHouse → Flatfile",
    desc: "Export and ingest analytics-ready data from ClickHouse directly into Flatfile with ease.",
  },
  {
    icon: <Export size={28} className="text-violet-500" />,
    title: "Flatfile → ClickHouse",
    desc: "Effortlessly move customer CSVs and files into high-performance ClickHouse tables.",
  },
  {
    icon: <SlidersHorizontal size={28} className="text-primary" />,
    title: "Fully Configurable",
    desc: "Customizable mappings, scheduling & validation to fit your workflow.",
  },
  {
    icon: <Check size={28} className="text-green-500" />,
    title: "Reliable and Secure",
    desc: "Proven infrastructure with transparent monitoring and end-to-end encryption.",
  },
];

const Features = () => (
  <section className="grid md:grid-cols-2 gap-8 my-10">
    {features.map((f, i) => (
      <div
        key={f.title}
        className="flex items-start gap-4 p-6 bg-white rounded-2xl shadow-md hover:shadow-lg transition-shadow animate-fade-in"
        style={{ animationDelay: `${0.05 * i}s` }}
      >
        <div>{f.icon}</div>
        <div>
          <h3 className="text-xl font-bold mb-1 text-gray-800">{f.title}</h3>
          <p className="text-gray-600">{f.desc}</p>
        </div>
      </div>
    ))}
  </section>
);

export default Features;