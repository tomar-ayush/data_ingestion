import { Button } from "@/components/ui/button";
import Link from "next/link";

const Hero = () => (
  <section className="w-full flex flex-col items-center justify-center py-20 bg-white">
    <h1 className="text-4xl md:text-5xl font-extrabold tracking-tight text-gray-900 mb-4 animate-fade-in">
      Bi-Directional Data Ingestion
      <span className="block text-primary mt-1">ClickHouse &lt;→&gt; Flatfile</span>
    </h1>
    <p className="max-w-xl text-center text-lg text-gray-600 mb-8 animate-fade-in" style={{ animationDelay: "0.1s" }}>
      Seamlessly transfer data between ClickHouse and Flatfile in either direction.<br />
      Reliable, lightning-fast, and beautifully simple.
    </p>
    <Link href="/auth">
      <Button size="lg" className="px-8 py-6 text-lg shadow-lg animate-scale-in">
        Get Started
      </Button>
    </Link>
  </section>
);

export default Hero;